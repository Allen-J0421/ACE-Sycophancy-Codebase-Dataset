/**
 * Comprehensive usage documentation and examples for the LCS ecosystem.
 * Demonstrates all major features with runnable code examples.
 */
class LcsDocumentation {

    public static void main(String[] args) {
        System.out.println("=".repeat(70));
        System.out.println("LCS Ecosystem - Comprehensive Usage Guide");
        System.out.println("=".repeat(70));
        System.out.println();

        exampleBasicUsage();
        exampleWithConfiguration();
        exampleWithNormalization();
        exampleBatchProcessing();
        exampleAnalysis();
        exampleDiffing();
        exampleValidation();
        exampleAdaptiveSelection();
        exampleCaching();
        examplePerformanceProfiling();

        System.out.println("=".repeat(70));
        System.out.println("All examples completed successfully!");
        System.out.println("=".repeat(70));
    }

    static void exampleBasicUsage() {
        System.out.println("1. BASIC USAGE - Simple LCS computation");
        System.out.println("-".repeat(70));

        // Get just the length
        int length = LcsQueries.compare("AGGTAB", "GXTXAYB").length();
        System.out.println("LCS length: " + length);

        // Get the actual sequence
        String sequence = LcsQueries.compare("AGGTAB", "GXTXAYB").sequence();
        System.out.println("LCS sequence: " + sequence);

        // Check similarity
        double similarity = LcsQueries.compare("AGGTAB", "GXTXAYB").similarity();
        System.out.println(String.format("Similarity: %.1f%%", similarity));

        System.out.println();
    }

    static void exampleWithConfiguration() {
        System.out.println("2. CONFIGURATION - Using preset configurations");
        System.out.println("-".repeat(70));

        // Case-insensitive comparison
        System.out.println("Case-insensitive comparison:");
        int result1 = LcsQueries.compare("HELLO", "hello").caseInsensitive().length();
        System.out.println("  LCS('HELLO', 'hello') = " + result1 + " (case-insensitive)");

        // Using lenient config
        System.out.println("\nLenient config:");
        LcsConfig config = LcsConfig.lenient();
        LcsSolver solver = new NormalizingLcsSolver(
                LcsSolverFactory.cached(),
                InputNormalizers.caseInsensitive()
        );
        int result2 = solver.solve(new LcsInput("HELLO", "hello")).getLength();
        System.out.println("  Result with lenient config: " + result2);

        // Using text-matching config (removes punctuation)
        System.out.println("\nText-matching config:");
        LcsConfig textConfig = LcsConfig.textMatching();
        System.out.println("  Config: " + textConfig);

        System.out.println();
    }

    static void exampleWithNormalization() {
        System.out.println("3. NORMALIZATION - Handling messy input");
        System.out.println("-".repeat(70));

        String messy1 = "Hello, World!";
        String messy2 = "hello world";

        System.out.println("Input 1: " + messy1);
        System.out.println("Input 2: " + messy2);

        // Direct comparison (strict)
        int strict = LcsQueries.compare(messy1, messy2).length();
        System.out.println("Strict comparison: " + strict);

        // With case-insensitive normalization
        int normalized = LcsQueries.compare(messy1, messy2)
                .caseInsensitive()
                .length();
        System.out.println("Case-insensitive: " + normalized);

        System.out.println();
    }

    static void exampleBatchProcessing() {
        System.out.println("4. BATCH PROCESSING - Compare multiple pairs");
        System.out.println("-".repeat(70));

        // Batch lengths
        System.out.println("Batch 1: Get multiple LCS lengths");
        int[] lengths = LcsQueries.batch(
                "ABC", "BCD",
                "HELLO", "HALLO",
                "AGGTAB", "GXTXAYB"
        ).lengths();
        System.out.println("  Lengths: [" + lengths[0] + ", " + lengths[1] + ", " + lengths[2] + "]");

        // Batch average similarity
        System.out.println("\nBatch 2: Average similarity");
        double avgSim = LcsQueries.batch(
                "IDENTICAL", "IDENTICAL",
                "ABC", "DEF",
                "HELLO", "HALLO"
        ).averageSimilarity();
        System.out.println(String.format("  Average similarity: %.1f%%", avgSim));

        System.out.println();
    }

    static void exampleAnalysis() {
        System.out.println("5. ANALYSIS - Comprehensive metrics");
        System.out.println("-".repeat(70));

        var analysis = LcsQueries.analyze("AGGTAB", "GXTXAYB");
        System.out.println(analysis);

        // Individual metrics
        System.out.println("\nIndividual metrics:");
        int len = LcsQueries.compare("ABC", "AXC").length();
        double dice = LcsQueries.compare("ABC", "AXC").dice();
        double jaccard = LcsQueries.compare("ABC", "AXC").jaccard();
        int editDist = LcsQueries.compare("ABC", "AXC").editDistance();

        System.out.println(String.format(
                "  LCS length: %d, Dice: %.3f, Jaccard: %.3f, Edit distance: %d",
                len, dice, jaccard, editDist
        ));

        System.out.println();
    }

    static void exampleDiffing() {
        System.out.println("6. DIFFING - Visualize differences");
        System.out.println("-".repeat(70));

        String s1 = "AGGTAB";
        String s2 = "GXTXAYB";
        String lcs = "GTAB";

        // ASCII diff
        System.out.println("ASCII Diff:");
        System.out.println(LcsDiffer.asciiDiff(s1, s2, lcs));

        // Side-by-side
        System.out.println("Side-by-side Diff:");
        System.out.println(LcsDiffer.sideBySideDiff(s1, s2, lcs));

        System.out.println();
    }

    static void exampleValidation() {
        System.out.println("7. VALIDATION - Input quality checks");
        System.out.println("-".repeat(70));

        // Standard validator
        System.out.println("Standard validation:");
        LcsValidator validator = LcsValidators.standard();
        ValidationResult result1 = validator.validate("HELLO", "WORLD");
        System.out.println("  Valid input: " + result1);

        // Large input warning
        String large = "X".repeat(6000);
        ValidationResult result2 = validator.validate(large, "ABC");
        System.out.println("  Large input: " + result2);

        // Validation failure
        ValidationResult result3 = validator.validate(null, "ABC");
        System.out.println("  Null input: " + result3);

        System.out.println();
    }

    static void exampleAdaptiveSelection() {
        System.out.println("8. ADAPTIVE SELECTION - Automatic optimization");
        System.out.println("-".repeat(70));

        AdaptiveSolver adaptive = new AdaptiveSolver();

        // Small input
        System.out.println("Small input (3×3):");
        AdaptiveSolver.InputProfile small = new AdaptiveSolver.InputProfile("ABC", "DEF");
        System.out.println("  Profile: " + small);
        System.out.println("  Recommended: " + adaptive.selectSolver(small));

        // Large input
        System.out.println("\nLarge input (5000×5000):");
        AdaptiveSolver.InputProfile large = new AdaptiveSolver.InputProfile(
                "A".repeat(5000),
                "B".repeat(5000)
        );
        System.out.println("  Profile: " + large);
        System.out.println("  Recommended: " + adaptive.selectSolver(large));

        // Asymmetric input
        System.out.println("\nAsymmetric input (7×100):");
        AdaptiveSolver.InputProfile asymmetric = new AdaptiveSolver.InputProfile(
                "PATTERN",
                "X".repeat(50) + "PATTERN" + "X".repeat(50)
        );
        System.out.println("  Profile: " + asymmetric);
        System.out.println("  Recommended: " + adaptive.selectSolver(asymmetric));

        System.out.println();
    }

    static void exampleCaching() {
        System.out.println("9. CACHING - Multiple caching strategies");
        System.out.println("-".repeat(70));

        // Standard caching
        System.out.println("Standard caching:");
        LcsSolver cachedSolver = LcsSolverFactory.cached();
        long start1 = System.nanoTime();
        cachedSolver.solve(new LcsInput("HELLO", "HALLO"));
        long first = System.nanoTime() - start1;

        long start2 = System.nanoTime();
        cachedSolver.solve(new LcsInput("HELLO", "HALLO"));
        long second = System.nanoTime() - start2;

        System.out.println(String.format("  First call: %.3f ms (computed)", first / 1_000_000.0));
        System.out.println(String.format("  Second call: %.3f μs (cached)", second / 1_000.0));

        // LRU caching
        System.out.println("\nLRU caching (capacity=5):");
        LruCachedLcsSolver lruSolver = new LruCachedLcsSolver(new StandardLcsSolver(), 5);
        for (int i = 0; i < 10; i++) {
            lruSolver.solve(new LcsInput("Test" + i, "Test"));
        }
        System.out.println("  Cache stats: " + lruSolver.getCacheStats());

        System.out.println();
    }

    static void examplePerformanceProfiling() {
        System.out.println("10. PERFORMANCE PROFILING - Measure and compare");
        System.out.println("-".repeat(70));

        String s1 = "AGGTAB";
        String s2 = "GXTXAYB";

        // Single execution
        System.out.println("Single execution metrics:");
        PerformanceProfiler.ExecutionMetrics metrics = PerformanceProfiler.profile(
                new StandardLcsSolver(), s1, s2
        );
        System.out.println("  " + metrics);

        // Multiple runs
        System.out.println("\nAggregate metrics (5 runs):");
        PerformanceProfiler.AggregateMetrics agg = PerformanceProfiler.profileMultiple(
                new StandardLcsSolver(), s1, s2, 5
        );
        System.out.println("  " + agg);

        // Solver comparison
        System.out.println("\nSolver comparison (3 runs each):");
        System.out.println("  Standard:       " + PerformanceProfiler.profileMultiple(
                new StandardLcsSolver(), s1, s2, 3).avgMillis() + " ms");
        System.out.println("  Space-optimized: " + PerformanceProfiler.profileMultiple(
                new SpaceOptimizedLcsSolver(), s1, s2, 3).avgMillis() + " ms");
        System.out.println("  Cached:         " + PerformanceProfiler.profileMultiple(
                LcsSolverFactory.cached(), s1, s2, 3).avgMillis() + " ms");

        System.out.println();
    }
}
