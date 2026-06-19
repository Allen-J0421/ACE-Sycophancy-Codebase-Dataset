/**
 * Shared utility for building DP (Dynamic Programming) tables.
 * Eliminates code duplication across multiple solver implementations.
 *
 * The DP table computes the length of LCS at each cell [i][j],
 * where dp[i][j] represents the LCS length of s1[0..i-1] and s2[0..j-1].
 */
class DpTableBuilder {

    /**
     * Builds the DP table for LCS computation.
     *
     * Algorithm: Fill table left-to-right, top-to-bottom.
     * - If characters match: extend previous diagonal result
     * - If no match: take maximum of left or top cell
     *
     * @param s1 first string (non-null)
     * @param s2 second string (non-null)
     * @return DP table where dp[m][n] contains LCS length
     */
    static int[][] buildTable(String s1, String s2) {
        int m = s1.length();
        int n = s2.length();
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

        return dpTable;
    }

    /**
     * Builds a DP table using a custom character matcher.
     * Enables case-insensitive, unicode-aware, or custom comparison logic.
     *
     * @param s1 first string (non-null)
     * @param s2 second string (non-null)
     * @param matcher custom character comparison function
     * @return DP table where dp[m][n] contains LCS length
     */
    static int[][] buildTableWithMatcher(String s1, String s2, CharacterMatcher matcher) {
        int m = s1.length();
        int n = s2.length();
        int[][] dpTable = new int[m + 1][n + 1];

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (matcher.matches(s1.charAt(i - 1), s2.charAt(j - 1))) {
                    dpTable[i][j] = dpTable[i - 1][j - 1] + 1;
                } else {
                    dpTable[i][j] = Math.max(dpTable[i - 1][j],
                                              dpTable[i][j - 1]);
                }
            }
        }

        return dpTable;
    }
}
