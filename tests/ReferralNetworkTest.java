import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Set;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

// Import the ReferralNetwork class from source directory
import java.nio.file.Paths;
import java.nio.file.Path;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.io.File;

/**
 * A comprehensive test suite for the ReferralNetwork class. [cite: 111]
 * It validates core functionality and all constraints from Part 1. 
 */
class ReferralNetworkTest {

    private ReferralNetwork network;

    @BeforeEach
    void setUp() {
        // A new instance is created before each test to ensure isolation.
        network = new ReferralNetwork();
    }

    @Test
    void addReferral_shouldSucceedForValidReferral() {
        assertTrue(network.addReferral("Alice", "Bob"), "Should allow a valid referral.");
        Set<String> referrals = network.getDirectReferrals("Alice");
        assertEquals(1, referrals.size(), "Alice should have 1 direct referral.");
        assertTrue(referrals.contains("Bob"), "Alice's referral should be Bob.");
    }

    @Test
    void getDirectReferrals_shouldReturnEmptySetForUserWithNoReferrals() {
        network.addReferral("Alice", "Bob");
        assertTrue(network.getDirectReferrals("Bob").isEmpty(), "Bob has not referred anyone, so his referrals should be an empty set.");
    }

    // --- CONSTRAINT TESTS ---

    @Test
    void addReferral_shouldFailOnSelfReferral() {
        assertFalse(network.addReferral("Alice", "Alice"), "A user should not be able to refer themselves.");
        assertTrue(network.getDirectReferrals("Alice").isEmpty(), "Graph should not be modified after a failed self-referral.");
    }

    @Test
    void addReferral_shouldFailWhenCandidateIsAlreadyReferred() {
        network.addReferral("Alice", "Bob"); // Alice refers Bob successfully.
        assertFalse(network.addReferral("Charlie", "Bob"), "Charlie should not be able to refer Bob, who is already referred.");
        
        Set<String> aliceReferrals = network.getDirectReferrals("Alice");
        assertEquals(1, aliceReferrals.size());
        assertTrue(aliceReferrals.contains("Bob"));

        assertTrue(network.getDirectReferrals("Charlie").isEmpty(), "Charlie's failed referral should not be added.");
    }

    @Test
    void addReferral_shouldFailOnSimpleCycle() {
        assertTrue(network.addReferral("Alice", "Bob"));
        assertFalse(network.addReferral("Bob", "Alice"), "Adding a referral from Bob to Alice should create a cycle and fail.");
        
        assertEquals(1, network.getDirectReferrals("Alice").size());
        assertTrue(network.getDirectReferrals("Bob").isEmpty());
    }

    @Test
    void addReferral_shouldFailOnComplexCycle() {
        // Create a path: Alice -> Bob -> Charlie -> David
        network.addReferral("Alice", "Bob");
        network.addReferral("Bob", "Charlie");
        network.addReferral("Charlie", "David");

        // Attempt to close the loop: David -> Alice
        assertFalse(network.addReferral("David", "Alice"), "Adding a referral from David to Alice should create a long cycle and fail.");
        
        assertTrue(network.getDirectReferrals("David").isEmpty(), "David's failed referral should not be recorded.");
    }

    @Test
    void getDirectReferrals_returnsUnmodifiableSet() {
        network.addReferral("Alice", "Bob");
        Set<String> referrals = network.getDirectReferrals("Alice");

        // This should throw an UnsupportedOperationException
        assertThrows(UnsupportedOperationException.class, () -> {
            referrals.add("Charlie");
        }, "The returned set of referrals should be unmodifiable.");
    }

    // ===== PART 2: FULL NETWORK REACH TESTS =====

    @Test
    void getTotalReferralCount_shouldReturnZeroForUserWithNoReferrals() {
        assertEquals(0, network.getTotalReferralCount("Alice"), "User with no referrals should have count of 0.");
    }

    @Test
    void getTotalReferralCount_shouldReturnZeroForNonExistentUser() {
        assertEquals(0, network.getTotalReferralCount("NonExistent"), "Non-existent user should have count of 0.");
    }

    @Test
    void getTotalReferralCount_shouldCountDirectReferralsOnly() {
        network.addReferral("Alice", "Bob");
        network.addReferral("Alice", "Charlie");
        
        assertEquals(2, network.getTotalReferralCount("Alice"), "Alice should have 2 total referrals.");
        assertEquals(0, network.getTotalReferralCount("Bob"), "Bob should have 0 total referrals.");
    }

    @Test
    void getTotalReferralCount_shouldCountIndirectReferrals() {
        // Create chain: Alice -> Bob -> Charlie -> David
        network.addReferral("Alice", "Bob");
        network.addReferral("Bob", "Charlie");
        network.addReferral("Charlie", "David");
        
        assertEquals(3, network.getTotalReferralCount("Alice"), "Alice should have 3 total referrals (Bob, Charlie, David).");
        assertEquals(2, network.getTotalReferralCount("Bob"), "Bob should have 2 total referrals (Charlie, David).");
        assertEquals(1, network.getTotalReferralCount("Charlie"), "Charlie should have 1 total referral (David).");
        assertEquals(0, network.getTotalReferralCount("David"), "David should have 0 total referrals.");
    }

    @Test
    void getTotalReferralCount_shouldHandleComplexNetwork() {
        // Create a more complex network
        network.addReferral("Alice", "Bob");
        network.addReferral("Alice", "Charlie");
        network.addReferral("Bob", "David");
        network.addReferral("Charlie", "Eve");
        network.addReferral("Charlie", "Frank");
        
        assertEquals(5, network.getTotalReferralCount("Alice"), "Alice should have 5 total referrals.");
        assertEquals(1, network.getTotalReferralCount("Bob"), "Bob should have 1 total referral.");
        assertEquals(2, network.getTotalReferralCount("Charlie"), "Charlie should have 2 total referrals.");
    }

    @Test
    void getTopReferrersByReach_shouldReturnEmptyListForZeroK() {
        network.addReferral("Alice", "Bob");
        assertTrue(network.getTopReferrersByReach(0).isEmpty(), "Should return empty list for k=0.");
    }

    @Test
    void getTopReferrersByReach_shouldReturnEmptyListForNegativeK() {
        network.addReferral("Alice", "Bob");
        assertTrue(network.getTopReferrersByReach(-1).isEmpty(), "Should return empty list for negative k.");
    }

    @Test
    void getTopReferrersByReach_shouldRankByTotalReferralCount() {
        // Alice: 3 referrals (Bob, Charlie, David)
        network.addReferral("Alice", "Bob");
        network.addReferral("Bob", "Charlie");
        network.addReferral("Charlie", "David");
        
        // Eve: 1 referral (Frank)
        network.addReferral("Eve", "Frank");
        
        List<String> topReferrers = network.getTopReferrersByReach(3);
        assertEquals(3, topReferrers.size(), "Should return 3 referrers.");
        assertEquals("Alice", topReferrers.get(0), "Alice should be ranked first with 3 referrals.");
        assertEquals("Bob", topReferrers.get(1), "Bob should be ranked second with 2 referrals.");
        assertTrue(topReferrers.contains("Charlie") || topReferrers.contains("Eve"), "Third place should be Charlie or Eve.");
    }

    @Test
    void getTopReferrersByReach_shouldLimitToKResults() {
        network.addReferral("Alice", "Bob");
        network.addReferral("Charlie", "David");
        network.addReferral("Eve", "Frank");
        
        List<String> topReferrers = network.getTopReferrersByReach(2);
        assertEquals(2, topReferrers.size(), "Should return exactly 2 referrers when k=2.");
    }

    // ===== PART 3: INFLUENCER IDENTIFICATION TESTS =====

    @Test
    void getUniqueReachExpansion_shouldReturnEmptyListForZeroK() {
        network.addReferral("Alice", "Bob");
        assertTrue(network.getUniqueReachExpansion(0).isEmpty(), "Should return empty list for k=0.");
    }

    @Test
    void getUniqueReachExpansion_shouldSelectUserWithLargestReach() {
        // Alice: reaches Bob, Charlie, David
        network.addReferral("Alice", "Bob");
        network.addReferral("Bob", "Charlie");
        network.addReferral("Charlie", "David");
        
        // Eve: reaches only Frank
        network.addReferral("Eve", "Frank");
        
        List<String> selected = network.getUniqueReachExpansion(1);
        assertEquals(1, selected.size(), "Should select 1 referrer.");
        assertEquals("Alice", selected.get(0), "Should select Alice as she has the largest reach.");
    }

    @Test
    void getUniqueReachExpansion_shouldMinimizeOverlap() {
        // Create overlapping networks
        network.addReferral("Alice", "Bob");
        network.addReferral("Alice", "Charlie");
        network.addReferral("David", "Eve");
        network.addReferral("David", "Frank");
        
        List<String> selected = network.getUniqueReachExpansion(2);
        assertEquals(2, selected.size(), "Should select 2 referrers.");
        assertTrue(selected.contains("Alice"), "Should include Alice.");
        assertTrue(selected.contains("David"), "Should include David for non-overlapping reach.");
    }

    @Test
    void getFlowCentrality_shouldReturnEmptyListForZeroK() {
        network.addReferral("Alice", "Bob");
        assertTrue(network.getFlowCentrality(0).isEmpty(), "Should return empty list for k=0.");
    }

    @Test
    void getFlowCentrality_shouldIdentifyBrokers() {
        // Create a network where Bob is a critical broker
        // Alice -> Bob -> Charlie
        // David -> Bob -> Eve
        network.addReferral("Alice", "Bob");
        network.addReferral("Bob", "Charlie");
        network.addReferral("David", "Bob");
        network.addReferral("Bob", "Eve");
        
        List<String> brokers = network.getFlowCentrality(3);
        assertFalse(brokers.isEmpty(), "Should identify brokers.");
        // Bob should have high centrality as he's on many shortest paths
    }

    @Test
    void getFlowCentrality_shouldHandleLinearChain() {
        // Create linear chain: Alice -> Bob -> Charlie -> David
        network.addReferral("Alice", "Bob");
        network.addReferral("Bob", "Charlie");
        network.addReferral("Charlie", "David");
        
        List<String> brokers = network.getFlowCentrality(2);
        assertEquals(2, brokers.size(), "Should return 2 brokers.");
        // Bob and Charlie should have higher centrality than Alice and David
    }

    @Test
    void getFlowCentrality_shouldLimitToKResults() {
        network.addReferral("Alice", "Bob");
        network.addReferral("Bob", "Charlie");
        network.addReferral("Charlie", "David");
        network.addReferral("David", "Eve");
        
        List<String> brokers = network.getFlowCentrality(2);
        assertEquals(2, brokers.size(), "Should return exactly 2 brokers when k=2.");
    }

    // ===== INTEGRATION TESTS =====

    @Test
    void integrationTest_complexNetworkAllMetrics() {
        // Build a complex network for comprehensive testing
        network.addReferral("CEO", "Manager1");
        network.addReferral("CEO", "Manager2");
        network.addReferral("Manager1", "Employee1");
        network.addReferral("Manager1", "Employee2");
        network.addReferral("Manager2", "Employee3");
        network.addReferral("Employee1", "Intern1");
        network.addReferral("Employee2", "Intern2");
        
        // Test total referral counts
        assertEquals(7, network.getTotalReferralCount("CEO"), "CEO should have 7 total referrals.");
        assertEquals(4, network.getTotalReferralCount("Manager1"), "Manager1 should have 4 total referrals.");
        assertEquals(1, network.getTotalReferralCount("Manager2"), "Manager2 should have 1 total referral.");
        
        // Test top referrers
        List<String> topReferrers = network.getTopReferrersByReach(3);
        assertEquals("CEO", topReferrers.get(0), "CEO should be top referrer.");
        assertEquals("Manager1", topReferrers.get(1), "Manager1 should be second.");
        
        // Test unique reach expansion
        List<String> uniqueReach = network.getUniqueReachExpansion(2);
        assertFalse(uniqueReach.isEmpty(), "Should select referrers for unique reach.");
        
        // Test flow centrality
        List<String> brokers = network.getFlowCentrality(3);
        assertFalse(brokers.isEmpty(), "Should identify brokers in the network.");
    }
}