/**
 * Quick tests for batch processing and analysis utilities.
 */
class TestBatchAndAnalysis {

    public static void main(String[] args) {
        testBatchProcessing();
        testAnalysis();
        System.out.println("\n✓ All tests passed");
    }

    static void testBatchProcessing() {
        System.out.println("=== Batch Processing Tests ===\n");

        String[] strings1 = {"AGGTAB", "ABCDEF", "ABC"};
        String[] strings2 = {"GXTXAYB", "FBDAMN", "DEF"};

        // Test sequential processor
        System.out.println("Sequential Processor:");
        LcsBatchProcessor sequential = new SequentialBatchProcessor(
                LcsSolverFactory.standard()
        );
        BatchResult result1 = sequential.processBatch(strings1, strings2);
        System.out.println(result1);
        assert result1.processedCount == 3 : "Should process 3 pairs";
        assert result1.lcsLengths[0] == 4 : "First pair should have LCS length 4";

        // Test cached processor
        System.out.println("\nCached Processor (with duplicates):");
        String[] strings3 = {"AGGTAB", "AGGTAB", "ABC"}; // First pair repeated
        String[] strings4 = {"GXTXAYB", "GXTXAYB", "DEF"};
        LcsBatchProcessor cached = new CachedBatchProcessor(
                LcsSolverFactory.standard()
        );
        BatchResult result2 = cached.processBatch(strings3, strings4);
        System.out.println(result2);
        assert result2.lcsLengths[0] == result2.lcsLengths[1] : "Duplicate pairs should have same result";

        // Test parallel processor
        System.out.println("\nParallel Processor:");
        LcsBatchProcessor parallel = new ParallelBatchProcessor(
                LcsSolverFactory.standard(),
                2  // Threshold: use parallelization for 2+ pairs
        );
        BatchResult result3 = parallel.processBatch(strings1, strings2);
        System.out.println(result3);
        assert result3.processedCount == 3 : "Should process 3 pairs";

        System.out.println();
    }

    static void testAnalysis() {
        System.out.println("=== LCS Analysis Tests ===\n");

        // Test with AGGTAB vs GXTXAYB (LCS length = 4)
        int lcsLen = 4;
        int len1 = 6;
        int len2 = 7;

        System.out.println(String.format("LCS(\"AGGTAB\", \"GXTXAYB\") = %d", lcsLen));
        System.out.println();

        // Test similarity metrics
        double similarity = LcsAnalyzer.similarityRatio(lcsLen, len1, len2);
        System.out.printf("Similarity Ratio: %.1f%%\n", similarity);
        assert similarity == 66.67 : "Similarity should be ~66.67%";

        double dice = LcsAnalyzer.diceCoefficient(lcsLen, len1, len2);
        System.out.printf("Dice Coefficient: %.3f\n", dice);
        assert dice > 0.3 && dice < 0.4 : "Dice should be ~0.36";

        double jaccard = LcsAnalyzer.jaccardSimilarity(lcsLen, len1, len2);
        System.out.printf("Jaccard Similarity: %.3f\n", jaccard);
        assert jaccard > 0.5 && jaccard < 0.7 : "Jaccard should be ~0.57";

        int editDist = LcsAnalyzer.estimatedEditDistance(lcsLen, len1, len2);
        System.out.printf("Estimated Edit Distance: %d\n", editDist);
        assert editDist == 5 : "Edit distance should be (6-4)+(7-4)=5";

        int maxDiff = LcsAnalyzer.longestDifferenceLength(lcsLen, len1, len2);
        System.out.printf("Max Difference: %d\n", maxDiff);
        assert maxDiff == 3 : "Max difference should be 7-4=3";

        // Test comprehensive analysis
        System.out.println();
        LcsAnalyzer.LcsAnalysisResult analysis =
                LcsAnalyzer.analyzeAll(lcsLen, len1, len2);
        System.out.println("Comprehensive Analysis:");
        System.out.println(analysis);
        System.out.println();
    }
}
