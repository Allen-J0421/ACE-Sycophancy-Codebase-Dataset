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
     * 2. Backtrack from dp[m][n] to dp[0][0], building result in reverse
     * 3. Reverse final result (more efficient than repeated insert(0, ...))
     *
     * Time complexity: O(m * n) for DP + O(m + n) for backtracking + O(k) for reverse
     *   where k = LCS length. Total: O(m * n) since k ≤ min(m,n)
     * Space complexity: O(m * n) for DP table + O(min(m,n)) for result
     *
     * Note: Using append + reverse is O(n) total vs insert(0,...) which is O(n²)
     *       for LCS of length n due to repeated character shifting.
     *
     * @param s1 first string
     * @param s2 second string
     * @return the LCS string
     * @throws IllegalArgumentException if either string is null
     */
    public static String reconstructLcs(String s1, String s2) {
        if (s1 == null) {
            throw new IllegalArgumentException("First string cannot be null");
        }
        if (s2 == null) {
            throw new IllegalArgumentException("Second string cannot be null");
        }

        int m = s1.length();
        int n = s2.length();

        // Build DP table using shared utility
        int[][] dpTable = DpTableBuilder.buildTable(s1, s2);

        // Backtrack to reconstruct the sequence
        // Build in reverse order for O(1) append instead of O(n) insert
        StringBuilder lcs = new StringBuilder();
        int i = m;
        int j = n;

        while (i > 0 && j > 0) {
            // If characters match, include in result and move diagonally
            if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                lcs.append(s1.charAt(i - 1));
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

        // Reverse to get correct order (we built it backwards for efficiency)
        return lcs.reverse().toString();
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
