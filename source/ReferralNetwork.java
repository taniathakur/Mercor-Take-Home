import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Comparator;


public class ReferralNetwork {

    private final Map<String, Set<String>> graph;
    private final Map<String, String> referredCandidates;

    public ReferralNetwork() {
        this.graph = new HashMap<>();
        this.referredCandidates = new HashMap<>();
    }

   
    public boolean addReferral(String referrer, String candidate) {
        
        // No Self-Referrals 
        if (referrer.equals(candidate)) {
            System.err.println("Error: A user cannot refer themselves.");
            return false;
        }

        //  Single candidate can only be referred by one user
        if (referredCandidates.containsKey(candidate)) {
            System.err.println("Error: Candidate '" + candidate + "' has already been referred by '" + referredCandidates.get(candidate) + "'.");
            return false;
        }

        // The graph must remain acyclic
        if (pathExists(candidate, referrer)) {
            System.err.println("Error: Adding this referral would create a cycle in the network.");
            return false;
        }

        // If all constraints pass, add the referral
        graph.computeIfAbsent(referrer, k -> new HashSet<>()).add(candidate);
        referredCandidates.put(candidate, referrer);

        return true;
    }

    
    // An unmodifiable set of the user's direct referrals. Returns an empty set if the user has no referrals.
    public Set<String> getDirectReferrals(String user) {
        return Collections.unmodifiableSet(graph.getOrDefault(user, Collections.emptySet()));
    }


    
    // BFS For finding the path between two nodes
    private boolean pathExists(String startNode, String endNode) {
        if (!graph.containsKey(startNode)) {
            return false;
        }

        Queue<String> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();

        queue.add(startNode);
        visited.add(startNode);

        while (!queue.isEmpty()) {
            String currentUser = queue.poll();

            if (currentUser.equals(endNode)) {
                return true;
            }

            for (String neighbor : getDirectReferrals(currentUser)) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }
        return false;
    }

    // PART 2: FULL NETWORK REACH =====

    /**
     * Calculates the total number of referrals for a given user, including both direct and indirect referrals.
     * Uses BFS to traverse the entire downstream network.
     * 
     * Business Scenario: Use this metric for commission calculations or performance bonuses
     * where you want to reward users for their entire network's growth, not just direct referrals.
     * 
     * @param user The user whose total referral count to calculate
     * @return The total number of direct and indirect referrals
     */
    public int getTotalReferralCount(String user) {
        if (!graph.containsKey(user)) {
            return 0;
        }

        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();
        
        queue.add(user);
        visited.add(user);
        
        while (!queue.isEmpty()) {
            String currentUser = queue.poll();
            
            for (String referral : getDirectReferrals(currentUser)) {
                if (!visited.contains(referral)) {
                    visited.add(referral);
                    queue.add(referral);
                }
            }
        }
        
        // Subtract 1 to exclude the user themselves from the count
        return visited.size() - 1;
    }

    /**
     * Returns a ranked list of the top k referrers based on their total referral count.
     * 
     * Business Scenario: Use this for leaderboards, recognition programs, or identifying
     * your most influential users for special partnerships or exclusive opportunities.
     * 
     * How to choose k: Consider your business context:
     * - For monthly recognition: k = 10-20 (manageable for personal outreach)
     * - For quarterly rewards: k = 50-100 (broader recognition program)
     * - For annual analysis: k = 100+ (comprehensive influence mapping)
     * 
     * @param k The number of top referrers to return
     * @return A list of users ranked by total referral count (highest first)
     */
    public List<String> getTopReferrersByReach(int k) {
        if (k <= 0) {
            return new ArrayList<>();
        }
        
        return graph.keySet().stream()
            .map(user -> new UserReachPair(user, getTotalReferralCount(user)))
            .sorted(Comparator.comparingInt(UserReachPair::getReach).reversed())
            .limit(k)
            .map(UserReachPair::getUser)
            .collect(Collectors.toList());
    }

    // Helper class for ranking users by reach
    private static class UserReachPair {
        private final String user;
        private final int reach;
        
        public UserReachPair(String user, int reach) {
            this.user = user;
            this.reach = reach;
        }
        
        public String getUser() { return user; }
        public int getReach() { return reach; }
    }

    // ===== PART 3: IDENTIFY INFLUENCERS =====

    public List<String> getUniqueReachExpansion(int k) {
        if (k <= 0) {
            return new ArrayList<>();
        }
        
        // Pre-compute full downstream reach for every user
        Map<String, Set<String>> userReachSets = new HashMap<>();
        for (String user : graph.keySet()) {
            userReachSets.put(user, getFullReachSet(user));
        }
        
        List<String> selectedReferrers = new ArrayList<>();
        Set<String> globalReachedSet = new HashSet<>();
        
        // Greedy algorithm: iteratively select user who adds most new candidates
        for (int i = 0; i < k && !userReachSets.isEmpty(); i++) {
            String bestUser = null;
            int maxNewCandidates = 0;
            
            for (Map.Entry<String, Set<String>> entry : userReachSets.entrySet()) {
                String user = entry.getKey();
                Set<String> userReach = entry.getValue();
                
                // Count how many new candidates this user would add
                int newCandidates = 0;
                for (String candidate : userReach) {
                    if (!globalReachedSet.contains(candidate)) {
                        newCandidates++;
                    }
                }
                
                if (newCandidates > maxNewCandidates) {
                    maxNewCandidates = newCandidates;
                    bestUser = user;
                }
            }
            
            if (bestUser != null && maxNewCandidates > 0) {
                selectedReferrers.add(bestUser);
                globalReachedSet.addAll(userReachSets.get(bestUser));
                userReachSets.remove(bestUser);
            } else {
                break; // No more users can add new candidates
            }
        }
        
        return selectedReferrers;
    }

    /**
     * Helper method to get the full downstream reach set for a user
     */
    private Set<String> getFullReachSet(String user) {
        Set<String> reachSet = new HashSet<>();
        if (!graph.containsKey(user)) {
            return reachSet;
        }
        
        Queue<String> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        
        queue.add(user);
        visited.add(user);
        
        while (!queue.isEmpty()) {
            String currentUser = queue.poll();
            
            for (String referral : getDirectReferrals(currentUser)) {
                if (!visited.contains(referral)) {
                    visited.add(referral);
                    queue.add(referral);
                    reachSet.add(referral); // Add to reach set (excluding the user themselves)
                }
            }
        }
        
        return reachSet;
    }

    /**
     * Metric 2: Flow Centrality
     * Identifies the network's most critical "brokers" - users who connect different parts
     * of the network and whose removal would most likely cause fragmentation.
     * 
     * Business Scenario: Use this to identify key relationship managers or community leaders
     * whose departure would significantly impact network connectivity. Critical for succession
     * planning, retention strategies, and understanding network vulnerabilities.
     * 
     * @param k The number of top brokers to return
     * @return A list of users ranked by their flow centrality (highest first)
     */
    public List<String> getFlowCentrality(int k) {
        if (k <= 0) {
            return new ArrayList<>();
        }
        
        // Get all users in the network
        Set<String> allUsers = new HashSet<>(graph.keySet());
        for (Set<String> referrals : graph.values()) {
            allUsers.addAll(referrals);
        }
        
        // Pre-compute shortest distances between all pairs of users
        Map<String, Map<String, Integer>> allDistances = new HashMap<>();
        for (String user : allUsers) {
            allDistances.put(user, computeShortestDistances(user));
        }
        
        // Calculate flow centrality for each user
        Map<String, Integer> centralityScores = new HashMap<>();
        for (String user : allUsers) {
            centralityScores.put(user, 0);
        }
        
        // For each combination of (source, target, broker)
        for (String source : allUsers) {
            for (String target : allUsers) {
                if (source.equals(target)) continue;
                
                Integer sourceToDest = allDistances.get(source).get(target);
                if (sourceToDest == null || sourceToDest == Integer.MAX_VALUE) continue;
                
                for (String broker : allUsers) {
                    if (broker.equals(source) || broker.equals(target)) continue;
                    
                    Integer sourceToBroker = allDistances.get(source).get(broker);
                    Integer brokerToTarget = allDistances.get(broker).get(target);
                    
                    if (sourceToBroker != null && brokerToTarget != null &&
                        sourceToBroker != Integer.MAX_VALUE && brokerToTarget != Integer.MAX_VALUE &&
                        sourceToBroker + brokerToTarget == sourceToDest) {
                        // Broker lies on shortest path from source to target
                        centralityScores.put(broker, centralityScores.get(broker) + 1);
                    }
                }
            }
        }
        
        // Return top k users by centrality score
        return centralityScores.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .limit(k)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }

    /**
     * Computes shortest distances from a source user to all other users using BFS
     */
    private Map<String, Integer> computeShortestDistances(String source) {
        Map<String, Integer> distances = new HashMap<>();
        Queue<String> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        
        queue.add(source);
        visited.add(source);
        distances.put(source, 0);
        
        while (!queue.isEmpty()) {
            String current = queue.poll();
            int currentDistance = distances.get(current);
            
            for (String neighbor : getDirectReferrals(current)) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                    distances.put(neighbor, currentDistance + 1);
                }
            }
        }
        
        return distances;
    }
}