/**
 * Fluent API for common LCS operations.
 * Simplifies common use cases with discoverable, chainable methods.
 * Reduces boilerplate for typical workflows.
 */
class LcsQueries {

    /**
     * Starts a comparison between two strings.
     * Returns a builder for fluent API usage.
     *
     * @param s1 first string
     * @param s2 second string
     * @return comparison builder
     */
    static ComparisonBuilder compare(String s1, String s2) {
        return new ComparisonBuilder(s1, s2);
    }

    /**
     * Builder for fluent comparison operations.
     */
    static class ComparisonBuilder {
        final String s1;
        final String s2;
        LcsSolver solver = LcsSolverFactory.standard();

        ComparisonBuilder(String s1, String s2) {
            this.s1 = s1;
            this.s2 = s2;
        }

        /**
         * Uses a specific solver for this comparison.
         */
        ComparisonBuilder withSolver(LcsSolver s) {
            this.solver = s;
            return this;
        }

        /**
         * Uses cached solver for better performance on similar comparisons.
         */
        ComparisonBuilder cached() {
            this.solver = LcsSolverFactory.cached();
            return this;
        }

        /**
         * Uses case-insensitive comparison.
         */
        ComparisonBuilder caseInsensitive() {
            this.solver = NormalizedSolvers.caseInsensitive();
            return this;
        }

        /**
         * Gets the LCS length.
         */
        int length() {
            LcsInput input = new LcsInput(s1, s2);
            return solver.solve(input).getLength();
        }

        /**
         * Gets the LCS sequence string.
         */
        String sequence() {
            return LcsSequenceReconstructor.reconstructLcs(s1, s2);
        }

        /**
         * Gets similarity ratio (LCS length / min length).
         */
        double similarity() {
            int lcs = length();
            return LcsAnalyzer.similarityRatio(lcs, s1.length(), s2.length());
        }

        /**
         * Gets Dice coefficient.
         */
        double dice() {
            int lcs = length();
            return LcsAnalyzer.diceCoefficient(lcs, s1.length(), s2.length());
        }

        /**
         * Gets Jaccard similarity.
         */
        double jaccard() {
            int lcs = length();
            return LcsAnalyzer.jaccardSimilarity(lcs, s1.length(), s2.length());
        }

        /**
         * Gets estimated edit distance.
         */
        int editDistance() {
            int lcs = length();
            return LcsAnalyzer.estimatedEditDistance(lcs, s1.length(), s2.length());
        }

        /**
         * Gets comprehensive analysis.
         */
        LcsAnalyzer.LcsAnalysisResult analyze() {
            int lcs = length();
            return LcsAnalyzer.analyzeAll(lcs, s1.length(), s2.length());
        }

        /**
         * Gets visual diff representation.
         */
        String diff() {
            String lcs = sequence();
            return LcsDiffer.visualDiff(s1, s2, lcs);
        }

        /**
         * Checks if strings are identical.
         */
        boolean areIdentical() {
            return s1.equals(s2);
        }

        /**
         * Checks if similarity meets threshold.
         *
         * @param threshold similarity percentage (0-100)
         */
        boolean meetsSimilarity(double threshold) {
            return similarity() >= threshold;
        }
    }

    /**
     * Creates a batch query builder for multiple pairs.
     *
     * @param pairs array of string pairs [s1, s2, s1, s2, ...]
     * @return batch builder
     */
    static BatchBuilder batch(String... pairs) {
        return new BatchBuilder(pairs);
    }

    /**
     * Builder for batch operations.
     */
    static class BatchBuilder {
        final String[] pairs;
        LcsSolver solver = LcsSolverFactory.standard();

        BatchBuilder(String... pairs) {
            if (pairs.length % 2 != 0) {
                throw new IllegalArgumentException("Pairs must be even-length array: [s1, s2, s1, s2, ...]");
            }
            this.pairs = pairs;
        }

        /**
         * Uses specific solver.
         */
        BatchBuilder withSolver(LcsSolver s) {
            this.solver = s;
            return this;
        }

        /**
         * Gets all LCS lengths.
         */
        int[] lengths() {
            int count = pairs.length / 2;
            int[] results = new int[count];

            for (int i = 0; i < count; i++) {
                LcsInput input = new LcsInput(pairs[2 * i], pairs[2 * i + 1]);
                results[i] = solver.solve(input).getLength();
            }

            return results;
        }

        /**
         * Gets all LCS sequences.
         */
        String[] sequences() {
            int count = pairs.length / 2;
            String[] results = new String[count];

            for (int i = 0; i < count; i++) {
                results[i] = LcsSequenceReconstructor.reconstructLcs(
                        pairs[2 * i], pairs[2 * i + 1]
                );
            }

            return results;
        }

        /**
         * Gets average similarity across all pairs.
         */
        double averageSimilarity() {
            int count = pairs.length / 2;
            double sum = 0;

            for (int i = 0; i < count; i++) {
                String s1 = pairs[2 * i];
                String s2 = pairs[2 * i + 1];
                LcsInput input = new LcsInput(s1, s2);
                int lcs = solver.solve(input).getLength();
                sum += LcsAnalyzer.similarityRatio(lcs, s1.length(), s2.length());
            }

            return count > 0 ? sum / count : 0;
        }
    }

    /**
     * Analyzes a single pair comprehensively.
     *
     * @param s1 first string
     * @param s2 second string
     * @return analysis result
     */
    static ComprehensiveAnalysis analyze(String s1, String s2) {
        return new ComprehensiveAnalysis(s1, s2);
    }

    /**
     * Comprehensive analysis of a string pair.
     */
    static class ComprehensiveAnalysis {
        final String s1;
        final String s2;
        final int lcsLength;
        final String lcsSequence;
        final LcsAnalyzer.LcsAnalysisResult metrics;

        ComprehensiveAnalysis(String s1, String s2) {
            this.s1 = s1;
            this.s2 = s2;
            this.lcsLength = LcsSolverFactory.standard().solve(new LcsInput(s1, s2)).getLength();
            this.lcsSequence = LcsSequenceReconstructor.reconstructLcs(s1, s2);
            this.metrics = LcsAnalyzer.analyzeAll(lcsLength, s1.length(), s2.length());
        }

        /**
         * Gets formatted report of all metrics.
         */
        @Override
        public String toString() {
            return String.format(
                    "LCS Analysis\n" +
                    "String 1: %s (length: %d)\n" +
                    "String 2: %s (length: %d)\n" +
                    "LCS: %s (length: %d)\n" +
                    "Similarity: %.1f%%\n" +
                    "Dice: %.3f\n" +
                    "Jaccard: %.3f\n" +
                    "Edit Distance: %d",
                    s1, s1.length(),
                    s2, s2.length(),
                    lcsSequence, lcsLength,
                    metrics.similarity,
                    metrics.dice,
                    metrics.jaccard,
                    metrics.editDistance
            );
        }
    }
}
