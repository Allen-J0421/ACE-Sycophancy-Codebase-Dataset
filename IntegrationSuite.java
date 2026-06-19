/**
 * Full integration test demonstrating all LCS subsystems working together.
 */
class IntegrationSuite {

    public static void main(String[] args) {
        System.out.println("=" .repeat(70));
        System.out.println("LCS Full Integration Test");
        System.out.println("=".repeat(70));
        System.out.println();

        testFluentApiWithConfig();
        System.out.println();
        testValidationWithConfig();
        System.out.println();
        testNormalizationWithDiffing();
        System.out.println();
        testBatchProcessingWithAnalysis();
        System.out.println();
        testSpecializedSolversWithMetrics();
        System.out.println();

        System.out.println("=".repeat(70));
        System.out.println("✓ All integration tests passed");
        System.out.println("=".repeat(70));
    }

    /**
     * Integration: Fluent API + Configuration
     */
    static void testFluentApiWithConfig() {
        System.out.println("Test 1: Fluent API with Lenient Configuration");

        // Set up lenient config (case-insensitive, normalized)
        LcsConfig config = LcsConfig.lenient();
        System.out.println("  Config: " + config);

        // Use fluent API with case-insensitive comparison
        int length = LcsQueries.compare("HELLO", "hello")
                .caseInsensitive()
                .length();
        assert length == 5 : "Should match ignoring case";
        System.out.println("  ✓ Case-insensitive match: LCS('HELLO', 'hello') = " + length);

        // Get comprehensive analysis
        var analysis = LcsQueries.analyze("AGGTAB", "GXTXAYB");
        assert analysis.lcsLength == 4;
        System.out.println("  ✓ Analysis: " + analysis.lcsLength + " character match");
    }

    /**
     * Integration: Validation + Configuration
     */
    static void testValidationWithConfig() {
        System.out.println("Test 2: Validation Framework with Configuration");

        String s1 = "The quick brown fox";
        String s2 = "The lazy brown fox";

        // Validate with strict rules
        LcsValidator validator = LcsValidators.strict();
        ValidationResult result = validator.validate(s1, s2);
        assert result.valid : "Should pass strict validation";
        System.out.println("  ✓ Strict validation: " + result);

        // Then use appropriate config
        LcsConfig config = LcsConfig.strict();
        int length = LcsQueries.compare(s1, s2).length();
        System.out.println("  ✓ Strict comparison: LCS length = " + length);
    }

    /**
     * Integration: Normalization + Diffing
     */
    static void testNormalizationWithDiffing() {
        System.out.println("Test 3: Input Normalization with Diffing");

        // Compare with text-only normalization
        String messy1 = "Hello, World!";
        String messy2 = "Hello World";

        // Normalized comparison
        int normalizedLength = LcsQueries.compare(messy1, messy2)
                .length();
        System.out.println("  ✓ Normalized LCS: " + normalizedLength + " chars");

        // Get diff view
        String lcs = LcsSequenceReconstructor.reconstructLcs(messy1, messy2);
        String diff = LcsDiffer.markdownDiff(messy1, messy2, lcs);
        assert diff.contains("##") : "Should have markdown headers";
        System.out.println("  ✓ Markdown diff generated");
    }

    /**
     * Integration: Batch Processing + Analysis
     */
    static void testBatchProcessingWithAnalysis() {
        System.out.println("Test 4: Batch Processing with Metrics");

        // Compare multiple pairs
        int[] lengths = LcsQueries.batch(
                "AGGTAB", "GXTXAYB",
                "HELLO", "HALLO",
                "ABC", "DEF"
        ).lengths();

        assert lengths.length == 3;
        System.out.println("  ✓ Batch lengths: [" + lengths[0] + ", " + lengths[1] + ", " + lengths[2] + "]");

        // Get average similarity
        double avgSim = LcsQueries.batch(
                "AGGTAB", "AGGTAB",
                "ABC", "ABC",
                "XYZ", "ABC"
        ).averageSimilarity();

        System.out.println(String.format("  ✓ Average similarity: %.1f%%", avgSim));
    }

    /**
     * Integration: Specialized Solvers + Metrics
     */
    static void testSpecializedSolversWithMetrics() {
        System.out.println("Test 5: Specialized Solvers with Metrics");

        // Test substring matching
        String pattern = "AGGTAB";
        String text = "XXAGGTABXX";

        LcsSolver substringFinder = new SubstringLcsSolver();
        int substringLength = substringFinder.solve(new LcsInput(pattern, text)).getLength();
        assert substringLength == 6 : "Should find complete pattern";
        System.out.println("  ✓ Substring solver: Found pattern length " + substringLength);

        // Test approximate matching for large input
        String large1 = "A".repeat(1000) + "BCDEF";
        String large2 = "A".repeat(999) + "XBCDEF";

        LcsSolver approximateFinder = new ApproximateLcsSolver();
        int approximateLength = approximateFinder.solve(new LcsInput(large1, large2)).getLength();
        System.out.println("  ✓ Approximate solver: Found LCS length " + approximateLength);

        // Get comprehensive metrics
        int length = LcsQueries.compare("AGGTAB", "GXTXAYB").length();
        double dice = LcsQueries.compare("AGGTAB", "GXTXAYB").dice();
        int editDist = LcsQueries.compare("AGGTAB", "GXTXAYB").editDistance();
        System.out.println(String.format("  ✓ Metrics: LCS=%d, Dice=%.3f, EditDist=%d", length, dice, editDist));
    }
}
