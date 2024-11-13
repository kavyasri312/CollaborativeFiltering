
import java.util.*;

public class CollaborativeFiltering {
    
    // Sample user ratings (Rows: Users, Columns: Movies)
    // 0 means the user hasn't rated the movie yet
    private static double[][] userRatings = {
        {5, 3, 0, 1},  // User 1
        {4, 0, 0, 1},  // User 2
        {1, 1, 0, 5},  // User 3
        {1, 0, 0, 4},  // User 4
        {0, 1, 5, 4},  // User 5
    };

    public static void main(String[] args) {
        int targetUser = 0; // Recommend for User 1
        List<Integer> recommendations = recommendItemsForUser(targetUser, userRatings);
        
        System.out.println("Recommended items for User " + (targetUser + 1) + ": " + recommendations);
    }

    // Function to recommend items for a user
    public static List<Integer> recommendItemsForUser(int userId, double[][] ratings) {
        int numUsers = ratings.length;
        int numItems = ratings[0].length;

        // Compute similarities between the target user and other users
        double[] userSimilarity = new double[numUsers];
        for (int i = 0; i < numUsers; i++) {
            if (i != userId) {
                userSimilarity[i] = cosineSimilarity(ratings[userId], ratings[i]);
            }
        }

        // Recommend items that similar users liked but the target user hasn't rated
        Map<Integer, Double> recommendedItems = new HashMap<>();

        for (int i = 0; i < numUsers; i++) {
            if (i != userId) {
                for (int item = 0; item < numItems; item++) {
                    if (ratings[userId][item] == 0 && ratings[i][item] > 0) {
                        recommendedItems.put(item, recommendedItems.getOrDefault(item, 0.0) + userSimilarity[i] * ratings[i][item]);
                    }
                }
            }
        }

        // Sort the recommended items by the computed score
        List<Map.Entry<Integer, Double>> sortedRecommendations = new ArrayList<>(recommendedItems.entrySet());
        sortedRecommendations.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        // Return the top recommended items
        List<Integer> recommendations = new ArrayList<>();
        for (Map.Entry<Integer, Double> entry : sortedRecommendations) {
            recommendations.add(entry.getKey() + 1); // Convert to 1-based index for items
        }

        return recommendations;
    }

    // Function to compute cosine similarity between two users
    public static double cosineSimilarity(double[] user1, double[] user2) {
        double dotProduct = 0.0, normA = 0.0, normB = 0.0;
        for (int i = 0; i < user1.length; i++) {
            dotProduct += user1[i] * user2[i];
            normA += Math.pow(user1[i], 2);
            normB += Math.pow(user2[i], 2);
        }

        if (normA == 0 || normB == 0) {
            return 0; // If a user hasn't rated anything, similarity is 0
        }

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}

