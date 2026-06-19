/**
 * Utility for analyzing LCS results and computing derived metrics.
 * Separates metric computation from I/O formatting, enabling reuse across contexts.
 */
class LcsAnalyzer {

    /**
     * Computes similarity ratio: LCS length / min(len(s1), len(s2))
     * Represents how much of the shorter string is in common.
     * Range: [0.0, 1.0]
     *
     * @param lcsLength the LCS length
     * @param s1Length  length of first string
     * @param s2Length  length of second string
     * @return similarity ratio (0-100%)
     */
    static double similarityRatio(int lcsLength, int s1Length, int s2Length) {
        int minLength = Math.min(s1Length, s2Length);
        if (minLength == 0) return 0;
        return (100.0 * lcsLength) / minLength;
    }

    /**
     * Computes Sørensen–Dice coefficient: 2 * LCS / (|s1| + |s2|)
     * Symmetric measure that weights common elements against total size.
     * Range: [0.0, 1.0]
     *
     * @param lcsLength length of LCS
     * @param s1Length  length of first string
     * @param s2Length  length of second string
     * @return Dice coefficient (0-1)
     */
    static double diceCoefficient(int lcsLength, int s1Length, int s2Length) {
        int totalLength = s1Length + s2Length;
        if (totalLength == 0) return 0;
        return (2.0 * lcsLength) / totalLength;
    }

    /**
     * Computes Jaccard similarity: LCS / max(|s1|, |s2|)
     * Represents overlap relative to the larger string.
     * Range: [0.0, 1.0]
     *
     * @param lcsLength length of LCS
     * @param s1Length  length of first string
     * @param s2Length  length of second string
     * @return Jaccard similarity (0-1)
     */
    static double jaccardSimilarity(int lcsLength, int s1Length, int s2Length) {
        int maxLength = Math.max(s1Length, s2Length);
        if (maxLength == 0) return 0;
        return (double) lcsLength / maxLength;
    }

    /**
     * Computes longest difference: max(|s1|, |s2|) - LCS
     * Represents how much of the longer string is different.
     *
     * @param lcsLength length of LCS
     * @param s1Length  length of first string
     * @param s2Length  length of second string
     * @return length of longest difference
     */
    static int longestDifferenceLength(int lcsLength, int s1Length, int s2Length) {
        return Math.max(s1Length, s2Length) - lcsLength;
    }

    /**
     * Computes common percentage: LCS / average(|s1|, |s2|) as percentage
     * Represents how much of an average string is common.
     * Useful for balanced comparison.
     *
     * @param lcsLength length of LCS
     * @param s1Length  length of first string
     * @param s2Length  length of second string
     * @return percentage of average length (0-100%)
     */
    static double commonPercentage(int lcsLength, int s1Length, int s2Length) {
        if (s1Length == 0 && s2Length == 0) return 0;
        double avgLength = (s1Length + s2Length) / 2.0;
        return (100.0 * lcsLength) / avgLength;
    }

    /**
     * Computes dissimilarity (opposite of similarity ratio).
     * Represents how much is NOT in common relative to shorter string.
     * Range: [0.0, 100.0]
     *
     * @param lcsLength length of LCS
     * @param s1Length  length of first string
     * @param s2Length  length of second string
     * @return dissimilarity percentage
     */
    static double dissimilarityPercentage(int lcsLength, int s1Length, int s2Length) {
        return 100.0 - similarityRatio(lcsLength, s1Length, s2Length);
    }

    /**
     * Estimates edit distance using LCS.
     * Formula: EditDistance ≈ (|s1| - LCS) + (|s2| - LCS)
     * This is an approximation; true edit distance may differ.
     * Assumes deletions and insertions only (no substitutions).
     *
     * @param lcsLength length of LCS
     * @param s1Length  length of first string
     * @param s2Length  length of second string
     * @return estimated edit distance (deletions + insertions)
     */
    static int estimatedEditDistance(int lcsLength, int s1Length, int s2Length) {
        return (s1Length - lcsLength) + (s2Length - lcsLength);
    }

    /**
     * Computes match ratio: how many characters in common relative to both strings.
     * Different from similarity ratio - includes both lengths.
     * Range: [0.0, 1.0]
     *
     * @param lcsLength length of LCS
     * @param s1Length  length of first string
     * @param s2Length  length of second string
     * @return match ratio
     */
    static double matchRatio(int lcsLength, int s1Length, int s2Length) {
        int maxLen = Math.max(Math.max(s1Length, s2Length), 1);
        return (double) lcsLength / maxLen;
    }

    /**
     * Computes all common metrics in a single analysis pass.
     *
     * @param lcsLength length of LCS
     * @param s1Length  length of first string
     * @param s2Length  length of second string
     * @return comprehensive analysis result
     */
    static LcsAnalysisResult analyzeAll(int lcsLength, int s1Length, int s2Length) {
        return new LcsAnalysisResult(
                lcsLength,
                s1Length,
                s2Length,
                similarityRatio(lcsLength, s1Length, s2Length),
                diceCoefficient(lcsLength, s1Length, s2Length),
                jaccardSimilarity(lcsLength, s1Length, s2Length),
                estimatedEditDistance(lcsLength, s1Length, s2Length),
                longestDifferenceLength(lcsLength, s1Length, s2Length)
        );
    }

    /**
     * Comprehensive analysis result object.
     */
    static class LcsAnalysisResult {
        final int lcsLength;
        final int s1Length;
        final int s2Length;
        final double similarity;      // Similarity ratio
        final double dice;            // Dice coefficient
        final double jaccard;         // Jaccard similarity
        final int editDistance;       // Estimated edit distance
        final int maxDifference;      // Longest difference

        LcsAnalysisResult(int lcsLength, int s1Length, int s2Length,
                         double similarity, double dice, double jaccard,
                         int editDistance, int maxDifference) {
            this.lcsLength = lcsLength;
            this.s1Length = s1Length;
            this.s2Length = s2Length;
            this.similarity = similarity;
            this.dice = dice;
            this.jaccard = jaccard;
            this.editDistance = editDistance;
            this.maxDifference = maxDifference;
        }

        @Override
        public String toString() {
            return String.format(
                    "LcsAnalysis(len=%d, sim=%.1f%%, dice=%.3f, jaccard=%.3f, editDist=%d)",
                    lcsLength, similarity, dice, jaccard, editDistance
            );
        }
    }
}
