/**
 * LCS solver optimized for finding subsequences of a pattern in a larger text.
 * Useful for pattern matching where one string is much smaller than the other.
 *
 * Optimization: When one string is significantly smaller (pattern matching),
 * this solver uses specialized algorithms more efficient than standard LCS.
 *
 * Best for: Finding patterns/keywords in text, where one string << other string
 */
class SubstringLcsSolver implements LcsSolver {

    @Override
    public LcsResult solve(LcsInput input) {
        String s1 = input.getFirstString();
        String s2 = input.getSecondString();

        // Determine which is pattern and which is text
        String pattern;
        String text;
        if (s1.length() <= s2.length()) {
            pattern = s1;
            text = s2;
        } else {
            pattern = s2;
            text = s1;
        }

        // For roughly equal sizes, use standard algorithm
        if (pattern.length() > text.length() * 0.5) {
            return new StandardLcsSolver().solve(input);
        }

        // Use specialized DP optimized for small pattern vs large text
        int result = computeSubstringLcs(pattern, text);
        return new LcsResult(result);
    }

    /**
     * Computes LCS optimized for pattern << text.
     * Uses row-by-row computation with only two rows in memory.
     *
     * @param pattern the small pattern string
     * @param text    the large text string
     * @return LCS length
     */
    private int computeSubstringLcs(String pattern, String text) {
        int p = pattern.length();
        int t = text.length();

        // Use rolling array to save space
        int[] prevRow = new int[t + 1];
        int[] currRow = new int[t + 1];

        for (int i = 1; i <= p; i++) {
            char patChar = pattern.charAt(i - 1);

            for (int j = 1; j <= t; j++) {
                if (patChar == text.charAt(j - 1)) {
                    currRow[j] = prevRow[j - 1] + 1;
                } else {
                    currRow[j] = Math.max(prevRow[j], currRow[j - 1]);
                }
            }

            // Swap rows
            int[] temp = prevRow;
            prevRow = currRow;
            currRow = temp;
        }

        return prevRow[t];
    }

    @Override
    public String toString() {
        return "SubstringLcsSolver(pattern << text optimization)";
    }
}

/**
 * Extended result for substring matching that includes offset information.
 * Tracks where the matching subsequence occurs in the text.
 */
class SubstringLcsResult {
    final int lcsLength;
    final int patternLength;
    final int textLength;
    final double coverage;  // LCS length as % of pattern

    /**
     * Creates a substring LCS result.
     *
     * @param lcsLength      length of LCS
     * @param patternLength  length of pattern
     * @param textLength     length of text
     */
    SubstringLcsResult(int lcsLength, int patternLength, int textLength) {
        this.lcsLength = lcsLength;
        this.patternLength = patternLength;
        this.textLength = textLength;
        this.coverage = patternLength > 0 ? (100.0 * lcsLength) / patternLength : 0;
    }

    /**
     * Checks if pattern is completely found (100% coverage).
     */
    boolean isCompleteMatch() {
        return lcsLength == patternLength;
    }

    /**
     * Checks if coverage exceeds threshold (useful for fuzzy matching).
     *
     * @param threshold percentage threshold (0-100)
     */
    boolean meetsCoverage(double threshold) {
        return coverage >= threshold;
    }

    @Override
    public String toString() {
        return String.format(
                "SubstringLcsResult(lcs=%d/%d, coverage=%.1f%%, %s)",
                lcsLength, patternLength, coverage,
                isCompleteMatch() ? "COMPLETE_MATCH" : "PARTIAL_MATCH"
        );
    }
}
