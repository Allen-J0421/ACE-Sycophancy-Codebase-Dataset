/**
 * Approximate LCS solver for very large inputs.
 * Uses diagonal band narrowing to reduce computation time.
 * Trades accuracy for speed on similar strings.
 *
 * Best for: Quick similarity filtering, large strings (>10,000 chars)
 * where strings are expected to be fairly similar.
 */
class ApproximateLcsSolver implements LcsSolver {
    private final double similarityThreshold;
    private final int bandWidth;

    /**
     * Creates an approximate solver with default parameters.
     * Assumes strings are >80% similar and uses 20% band width.
     */
    ApproximateLcsSolver() {
        this(0.80, 0.20);
    }

    /**
     * Creates an approximate solver with custom parameters.
     *
     * @param similarityThreshold expected similarity ratio (0.0-1.0)
     *                           determines band width for optimization
     * @param bandWidthPercent   band width as percentage of shorter string
     */
    ApproximateLcsSolver(double similarityThreshold, double bandWidthPercent) {
        this.similarityThreshold = Math.max(0.0, Math.min(1.0, similarityThreshold));
        this.bandWidth = (int) (100 * bandWidthPercent);
    }

    @Override
    public LcsResult solve(LcsInput input) {
        String s1 = input.getFirstString();
        String s2 = input.getSecondString();
        int m = s1.length();
        int n = s2.length();

        // For small inputs, use exact algorithm
        if (m + n < 1000) {
            return new StandardLcsSolver().solve(input);
        }

        // Estimate band width based on expected similarity
        int estimatedMinLcs = (int) (Math.min(m, n) * similarityThreshold);
        int band = Math.max(bandWidth, estimatedMinLcs / 10);

        // Use restricted DP with band
        int[][] dpTable = computeRestrictedDp(s1, s2, band);
        return new LcsResult(dpTable[m][n]);
    }

    /**
     * Computes DP table restricted to a diagonal band.
     * Only fills cells where |i - j| <= band, reducing computation.
     *
     * @param s1   first string
     * @param s2   second string
     * @param band width of diagonal band (in each direction from main diagonal)
     * @return DP table with LCS values
     */
    private int[][] computeRestrictedDp(String s1, String s2, int band) {
        int m = s1.length();
        int n = s2.length();
        int[][] dp = new int[m + 1][n + 1];

        for (int i = 1; i <= m; i++) {
            // Calculate column range for this row (within band)
            int jStart = Math.max(1, i - band);
            int jEnd = Math.min(n, i + band);

            for (int j = jStart; j <= jEnd; j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    // Handle boundary cases where neighbors are outside band
                    int left = (j > 1) ? dp[i][j - 1] : 0;
                    int top = (i > 1 && j >= jStart - 1) ? dp[i - 1][j] : 0;
                    dp[i][j] = Math.max(left, top);
                }
            }

            // Fill cells outside band with estimate
            if (jStart > 1) {
                for (int j = 1; j < jStart; j++) {
                    dp[i][j] = Math.max(dp[i][j - 1], dp[i - 1][j]);
                }
            }
            if (jEnd < n) {
                for (int j = jEnd + 1; j <= n; j++) {
                    dp[i][j] = Math.max(dp[i][j - 1], dp[i - 1][j]);
                }
            }
        }

        return dp;
    }

    @Override
    public String toString() {
        return String.format("ApproximateLcsSolver(threshold=%.0f%%, band=%d)",
                similarityThreshold * 100, bandWidth);
    }
}
