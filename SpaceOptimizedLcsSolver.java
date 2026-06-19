/**
 * Space-optimized LCS solver using rolling array technique.
 * Reduces space complexity from O(m*n) to O(min(m,n)) at the cost of
 * making sequence reconstruction impossible.
 *
 * Ideal for cases where only the length is needed and memory is a constraint.
 */
class SpaceOptimizedLcsSolver implements LcsSolver {

    /**
     * Computes the length of the Longest Common Subsequence using space-optimized DP.
     * Time complexity: O(m * n), Space complexity: O(min(m, n))
     *
     * Strategy: Use two 1D arrays (previous row and current row) instead of 2D table.
     * Since DP recurrence only depends on the previous row and current position,
     * we can compute column by column using only two rows of storage.
     *
     * @param input the LcsInput containing two strings
     * @return LcsResult with the length of the LCS
     */
    @Override
    public LcsResult solve(LcsInput input) {
        String s1 = input.getFirstString();
        String s2 = input.getSecondString();

        int m = s1.length();
        int n = s2.length();

        // Optimize space by using the shorter string as the inner dimension
        if (m < n) {
            return solveOptimized(s1, s2);
        } else {
            return solveOptimized(s2, s1);
        }
    }

    /**
     * Internal optimized solve ensuring s1 is the shorter string.
     *
     * @param s1 shorter string
     * @param s2 longer string
     * @return LcsResult with the length of the LCS
     */
    private LcsResult solveOptimized(String s1, String s2) {
        int m = s1.length();
        int n = s2.length();

        // Two rows: previous and current
        int[] previousRow = new int[m + 1];
        int[] currentRow = new int[m + 1];

        for (int j = 1; j <= n; j++) {
            for (int i = 1; i <= m; i++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    currentRow[i] = previousRow[i - 1] + 1;
                } else {
                    currentRow[i] = Math.max(previousRow[i], currentRow[i - 1]);
                }
            }

            // Swap rows: current becomes previous for next iteration
            int[] temp = previousRow;
            previousRow = currentRow;
            currentRow = temp;
        }

        // Result is in previousRow[m] (after final swap)
        return new LcsResult(previousRow[m]);
    }
}
