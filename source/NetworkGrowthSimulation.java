/**
 * NetworkGrowthSimulation.java
 * Implements simulation of referral network growth over time and bonus optimization.
 */

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class NetworkGrowthSimulation {
    private static final int INITIAL_REFERRERS = 100;
    private static final int REFERRAL_CAPACITY = 10;
    public static List<Double> simulate(double p, int days) {
        List<Double> cumulativeReferrals = new ArrayList<>();
        

        double activeReferrers = INITIAL_REFERRERS;
        double totalReferrals = 0;
        double usedCapacity = 0;
        
        // Add initial state (day 0)
        cumulativeReferrals.add(totalReferrals);
        
        // Simulate each day
        for (int day = 1; day <= days; day++) {
            // Calculate new referrals for this day
            double newReferrals = activeReferrers * p;
            
            // Update total referrals
            totalReferrals += newReferrals;
            
            // Update used capacity
            usedCapacity += newReferrals;
            
            // Calculate how many referrers become inactive
            double newInactiveReferrers = 0;
            if (usedCapacity > activeReferrers * REFERRAL_CAPACITY) {
                newInactiveReferrers = (usedCapacity - activeReferrers * REFERRAL_CAPACITY) / REFERRAL_CAPACITY;
                if (newInactiveReferrers > activeReferrers) {
                    newInactiveReferrers = activeReferrers;
                }
            }
            
            // Update active referrers (add new ones from referrals, subtract those who reached capacity)
            activeReferrers = activeReferrers + newReferrals - newInactiveReferrers;
            
            // Add to cumulative results
            cumulativeReferrals.add(totalReferrals);
        }
        
        return cumulativeReferrals;
    }
    
    /**
     * Calculates the minimum number of days required to reach a target number of referrals.
     * 
     * @param p The probability that an active referrer successfully refers someone on a given day
     * @param targetTotal The target number of total referrals to reach
     * @return The minimum number of days required to reach or exceed the target
     */
    public static int daysToTarget(double p, int targetTotal) {
        // Edge cases
        if (targetTotal <= 0) {
            return 0;
        }
        
        if (p <= 0) {
            return Integer.MAX_VALUE; // Impossible to reach target
        }
        
        // Binary search approach for efficiency
        int low = 0;
        int high = 10000; // Reasonable upper bound
        
        while (low < high) {
            int mid = low + (high - low) / 2;
            List<Double> simulation = simulate(p, mid);
            double result = simulation.get(simulation.size() - 1);
            
            if (result >= targetTotal) {
                high = mid;
            } else {
                low = mid + 1;
            }
        }
        
        return low;
    }
    
    /**
     * Calculates the minimum bonus amount required to achieve a target number of hires within a given timeframe.
     * 
     * @param days The number of days available to reach the target
     * @param targetHires The target number of hires to achieve
     * @param adoptionProb A function that maps bonus amount to referral probability
     * @param eps Precision parameter for calculations
     * @return The minimum bonus amount (rounded up to nearest $10) required to reach the target, or null if impossible
     */
    public static Integer minBonusForTarget(int days, int targetHires, Function<Integer, Double> adoptionProb, double eps) {
        // Edge cases
        if (targetHires <= 0) {
            return 0; // No hires needed
        }
        
        // Binary search for the minimum bonus
        int low = 0;
        int high = 10000; // $10,000 as a reasonable upper bound
        
        // Check if target is achievable with maximum bonus
        List<Double> maxSimulation = simulate(adoptionProb.apply(high), days);
        if (maxSimulation.get(maxSimulation.size() - 1) < targetHires) {
            return null; // Target is unachievable
        }
        
        while (low < high) {
            int mid = low + (high - low) / 2;
            double prob = adoptionProb.apply(mid);
            List<Double> simulation = simulate(prob, days);
            double result = simulation.get(simulation.size() - 1);
            
            if (result >= targetHires) {
                high = mid;
            } else {
                low = mid + 1;
            }
        }
        
        // Round up to nearest $10
        return ((low + 9) / 10) * 10;
    }
}