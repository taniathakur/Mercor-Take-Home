import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.function.Function;

public class NetworkGrowthSimulationTest {

    @Test
    public void testSimulate_ZeroProbability() {
        List<Double> result = NetworkGrowthSimulation.simulate(0.0, 10);
        assertEquals(11, result.size()); // Day 0 + 10 days
        for (Double total : result) {
            assertEquals(0.0, total, 0.001); // No referrals should occur
        }
    }

    @Test
    public void testSimulate_BasicGrowth() {
        List<Double> result = NetworkGrowthSimulation.simulate(0.1, 5);
        assertEquals(6, result.size()); // Day 0 + 5 days
        assertEquals(0.0, result.get(0), 0.001); // Day 0 starts with 0
        assertTrue(result.get(1) > 0); // Day 1 should have some referrals
        
        // Each day should have more cumulative referrals than the previous
        for (int i = 1; i < result.size(); i++) {
            assertTrue(result.get(i) >= result.get(i-1));
        }
    }

    @Test
    public void testDaysToTarget_ZeroTarget() {
        int days = NetworkGrowthSimulation.daysToTarget(0.1, 0);
        assertEquals(0, days); // Should take 0 days to reach 0 referrals
    }

    @Test
    public void testDaysToTarget_ImpossibleTarget() {
        int days = NetworkGrowthSimulation.daysToTarget(0.0, 100);
        assertEquals(Integer.MAX_VALUE, days); // Should be impossible with 0 probability
    }

    @Test
    public void testDaysToTarget_ReachableTarget() {
        int days = NetworkGrowthSimulation.daysToTarget(0.2, 50);
        assertTrue(days > 0); // Should take some days
        
        // Verify the result by simulating for the returned number of days
        List<Double> simulation = NetworkGrowthSimulation.simulate(0.2, days);
        assertTrue(simulation.get(simulation.size() - 1) >= 50);
        
        // Also verify that one fewer day is insufficient
        if (days > 1) {
            List<Double> insufficientSimulation = NetworkGrowthSimulation.simulate(0.2, days - 1);
            assertTrue(insufficientSimulation.get(insufficientSimulation.size() - 1) < 50);
        }
    }

    @Test
    public void testMinBonusForTarget_ZeroTarget() {
        Function<Integer, Double> adoptionProb = bonus -> 0.01 * (bonus / 100.0);
        Integer result = NetworkGrowthSimulation.minBonusForTarget(30, 0, adoptionProb, 0.001);
        assertEquals(0, result); // No bonus needed for 0 target
    }

    @Test
    public void testMinBonusForTarget_ImpossibleTarget() {
        // A function that always returns 0 probability
        Function<Integer, Double> zeroProb = bonus -> 0.0;
        Integer result = NetworkGrowthSimulation.minBonusForTarget(30, 100, zeroProb, 0.001);
        assertNull(result); // Should return null for impossible target
    }

    @Test
    public void testMinBonusForTarget_ReachableTarget() {
        // Linear adoption probability function
        Function<Integer, Double> linearProb = bonus -> Math.min(0.5, 0.01 * (bonus / 100.0));
        
        Integer result = NetworkGrowthSimulation.minBonusForTarget(30, 200, linearProb, 0.001);
        assertNotNull(result);
        assertTrue(result % 10 == 0); // Should be rounded to nearest $10
        
        // Verify the result
        double prob = linearProb.apply(result);
        List<Double> simulation = NetworkGrowthSimulation.simulate(prob, 30);
        assertTrue(simulation.get(simulation.size() - 1) >= 200);
        
        // Check that a smaller bonus is insufficient
        if (result >= 10) {
            double lowerProb = linearProb.apply(result - 10);
            List<Double> insufficientSimulation = NetworkGrowthSimulation.simulate(lowerProb, 30);
            assertTrue(insufficientSimulation.get(insufficientSimulation.size() - 1) < 200);
        }
    }

    @Test
    public void testIntegration_ComplexScenario() {
        // Test the entire workflow with a realistic scenario
        
        // Step 1: Define adoption probability function (sigmoid-like)
        Function<Integer, Double> sigmoidProb = bonus -> {
            double x = bonus / 100.0;
            return 0.5 / (1 + Math.exp(-0.5 * (x - 5))) + 0.01;
        };
        
        // Step 2: Find minimum bonus for a target
        int targetHires = 500;
        int timeframe = 60; // days
        Integer minBonus = NetworkGrowthSimulation.minBonusForTarget(timeframe, targetHires, sigmoidProb, 0.001);
        
        assertNotNull(minBonus);
        assertTrue(minBonus % 10 == 0); // Rounded to nearest $10
        
        // Step 3: Verify the minimum bonus achieves the target
        double finalProb = sigmoidProb.apply(minBonus);
        List<Double> finalSimulation = NetworkGrowthSimulation.simulate(finalProb, timeframe);
        assertTrue(finalSimulation.get(finalSimulation.size() - 1) >= targetHires);
        
        // Step 4: Calculate days to target with the determined probability
        int daysNeeded = NetworkGrowthSimulation.daysToTarget(finalProb, targetHires);
        assertTrue(daysNeeded <= timeframe); // Should be achievable within the timeframe
    }
}