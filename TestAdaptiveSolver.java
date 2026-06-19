/**
 * Tests for adaptive and self-optimizing solvers.
 */
class TestAdaptiveSolver {

    public static void main(String[] args) {
        testInputProfile();
        testAdaptiveSelection();
        testSelfOptimizing();
        System.out.println("\n✓ All adaptive solver tests passed");
    }

    static void testInputProfile() {
        System.out.println("=== Input Profile Analysis ===\n");

        // Test small input
        System.out.println("Test 1: Small input profile");
        AdaptiveSolver.InputProfile profile1 = new AdaptiveSolver.InputProfile("ABC", "DEF");
        assert profile1.maxLen == 3;
        assert profile1.lenRatio == 1.0;
        System.out.println("✓ " + profile1);

        // Test asymmetric input
        System.out.println("\nTest 2: Asymmetric input profile");
        AdaptiveSolver.InputProfile profile2 = new AdaptiveSolver.InputProfile(
                "PATTERN", "XXXXXXXXXXXXXXXXXXPATTERNXXXXXXXXXXXXXX"
        );
        assert profile2.lenRatio < 0.25;
        System.out.println("✓ " + profile2);

        // Test large input
        System.out.println("\nTest 3: Large input profile");
        AdaptiveSolver.InputProfile profile3 = new AdaptiveSolver.InputProfile(
                "A".repeat(5000),
                "B".repeat(5000)
        );
        assert profile3.maxLen == 5000;
        System.out.println("✓ " + profile3);

        // Test character diversity
        System.out.println("\nTest 4: Character diversity");
        AdaptiveSolver.InputProfile lowDiv = new AdaptiveSolver.InputProfile(
                "AAABBBAAABBB",
                "BBBAAABBBAA"
        );
        AdaptiveSolver.InputProfile highDiv = new AdaptiveSolver.InputProfile(
                "ABCDEFGHIJ",
                "JKLMNOPQRST"
        );
        assert lowDiv.charDiversity < highDiv.charDiversity;
        System.out.println("✓ Low diversity: " + String.format("%.2f", lowDiv.charDiversity));
        System.out.println("✓ High diversity: " + String.format("%.2f", highDiv.charDiversity));

        System.out.println();
    }

    static void testAdaptiveSelection() {
        System.out.println("=== Adaptive Solver Selection ===\n");

        AdaptiveSolver adaptive = new AdaptiveSolver();

        // Test selection for small input
        System.out.println("Test 1: Small input selection");
        AdaptiveSolver.InputProfile smallProfile = new AdaptiveSolver.InputProfile("ABC", "DEF");
        AdaptiveSolver.SolverRecommendation smallRec = adaptive.selectSolver(smallProfile);
        assert smallRec == AdaptiveSolver.SolverRecommendation.STANDARD;
        System.out.println("✓ Small: " + smallRec.description);

        // Test selection for asymmetric input
        System.out.println("\nTest 2: Asymmetric input selection");
        AdaptiveSolver.InputProfile asymmetricProfile = new AdaptiveSolver.InputProfile(
                "AB", "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXAB"
        );
        AdaptiveSolver.SolverRecommendation asymmetricRec = adaptive.selectSolver(asymmetricProfile);
        assert asymmetricRec == AdaptiveSolver.SolverRecommendation.SUBSTRING;
        System.out.println("✓ Asymmetric: " + asymmetricRec.description);

        // Test selection for very large input
        System.out.println("\nTest 3: Very large input selection");
        AdaptiveSolver.InputProfile largeProfile = new AdaptiveSolver.InputProfile(
                "A".repeat(6000),
                "B".repeat(6000)
        );
        AdaptiveSolver.SolverRecommendation largeRec = adaptive.selectSolver(largeProfile);
        assert largeRec == AdaptiveSolver.SolverRecommendation.APPROXIMATE;
        System.out.println("✓ Large: " + largeRec.description);

        // Test solving with adaptive selection
        System.out.println("\nTest 4: Adaptive solve");
        int result = adaptive.solve(new LcsInput("AGGTAB", "GXTXAYB")).getLength();
        assert result == 4;
        System.out.println("✓ Adaptive result: " + result);

        System.out.println();
    }

    static void testSelfOptimizing() {
        System.out.println("=== Self-Optimizing Solver ===\n");

        // Create with multiple candidates
        System.out.println("Test 1: Initialization");
        SelfOptimizingSolver selfOpt = new SelfOptimizingSolver(
                new StandardLcsSolver(),
                new SpaceOptimizedLcsSolver(),
                new ApproximateLcsSolver()
        );
        System.out.println("✓ Created: " + selfOpt);

        // Exploration phase: try each solver
        System.out.println("\nTest 2: Exploration phase (3 calls = 1 per solver)");
        selfOpt.solve(new LcsInput("ABC", "BCD"));
        selfOpt.solve(new LcsInput("HELLO", "HALLO"));
        selfOpt.solve(new LcsInput("AGGTAB", "GXTXAYB"));
        System.out.println("✓ Exploration complete");

        // Show initial stats
        System.out.println("\nTest 3: Performance stats after exploration");
        var stats = selfOpt.getStats();
        for (var entry : stats.entrySet()) {
            System.out.println("  " + entry.getKey() + ": " + entry.getValue() + " ns avg");
        }

        // Exploitation phase: solver will favor fastest
        System.out.println("\nTest 4: Exploitation phase (prefer fastest)");
        for (int i = 0; i < 5; i++) {
            int result = selfOpt.solve(new LcsInput("TEST", "BEST")).getLength();
            assert result == 3;
        }
        System.out.println("✓ Exploitation phase complete");

        // Final stats
        System.out.println("\nTest 5: Final stats after exploitation");
        var finalStats = selfOpt.getStats();
        for (var entry : finalStats.entrySet()) {
            System.out.println("  " + entry.getKey() + ": " + entry.getValue() + " ns avg");
        }

        System.out.println();
    }
}
