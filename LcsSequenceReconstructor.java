/**
 * Extends LCS capability to reconstruct the actual longest common subsequence string.
 * This class maintains the full DP table to enable backtracking through the solution.
 */
class LcsSequenceReconstructor {
    /**
     * Reconstructs the actual LCS string from two input strings.
     *
     * Algorithm:
     * 1. Compute DP table (same as standard LCS)
     * 2. Backtrack from dp[m][n] to dp[0][0]:
     *    - If chars match, char is part of LCS, move diagonally
     *    - If chars don't match, move in direction of larger value
     *
     * Time complexity: O(m * n) for DP + O(m + n) for backtracking = O(m * n)
     * Space complexity: O(m * n) for DP table + O(min(m,n)) for result
     *
     * @param s1 first string
     * @param s2 second string
     * @return the LCS string
     * @throws IllegalArgumentException if either string is null
     */
    public static String reconstructLcs(String s1, String s2) {
        if (s1 == null || s2 == null) {
            throw new IllegalArgumentException("Input strings cannot be null");
        }

        int m = s1.length();
        int n = s2.length();

        // Build DP table
        int[][] dpTable = new int[m + 1][n + 1];

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dpTable[i][j] = dpTable[i - 1][j - 1] + 1;
                } else {
                    dpTable[i][j] = Math.max(dpTable[i - 1][j],
                                              dpTable[i][j - 1]);
                }
            }
        }

        // Backtrack to reconstruct the sequence
        StringBuilder lcs = new StringBuilder();
        int i = m;
        int j = n;

        while (i > 0 && j > 0) {
            // If characters match, include in result and move diagonally
            if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                lcs.insert(0, s1.charAt(i - 1));
                i--;
                j--;
            }
            // Otherwise, move in the direction of the larger value
            else if (dpTable[i - 1][j] > dpTable[i][j - 1]) {
                i--;
            } else {
                j--;
            }
        }

        return lcs.toString();
    }

    /**
     * Convenience method using LcsInput.
     *
     * @param input the LcsInput containing two strings
     * @return the LCS string
     */
    public static String reconstructLcs(LcsInput input) {
        return reconstructLcs(input.getFirstString(), input.getSecondString());
    }
}
