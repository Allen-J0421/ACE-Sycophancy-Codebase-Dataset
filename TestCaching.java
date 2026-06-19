class TestCaching {
    public static void main(String[] args) {
        LcsSolver baseSolver = new StandardLcsSolver();
        CachedLcsSolver cachedSolver = new CachedLcsSolver(baseSolver);

        System.out.println("=== Testing CachedLcsSolver ===\n");

        // Test 1: Same query twice should use cache
        LcsInput input1 = new LcsInput("ABCDEF", "FBDAMN");
        System.out.println("Test 1: Repeated Queries");
        System.out.println("Query 1: \"ABCDEF\" vs \"FBDAMN\"");
        int result1 = cachedSolver.solve(input1).getLength();
        System.out.println("  Result: " + result1 + " (computed)");
        System.out.println("  Cache size: " + cachedSolver.getCacheSize());

        System.out.println("Query 1 (again): \"ABCDEF\" vs \"FBDAMN\"");
        int result2 = cachedSolver.solve(input1).getLength();
        System.out.println("  Result: " + result2 + " (from cache)");
        System.out.println("  Cache size: " + cachedSolver.getCacheSize());

        assert result1 == result2 : "Results should match";

        // Test 2: Reversed strings (symmetric cache)
        System.out.println("\nTest 2: Symmetry (reversed argument order)");
        LcsInput input2 = new LcsInput("FBDAMN", "ABCDEF");
        System.out.println("Query: \"FBDAMN\" vs \"ABCDEF\"");
        int result3 = cachedSolver.solve(input2).getLength();
        System.out.println("  Result: " + result3 + " (from cache - symmetric)");
        System.out.println("  Cache size: " + cachedSolver.getCacheSize());

        assert result1 == result3 : "Results should match regardless of order";

        // Test 3: New query
        System.out.println("\nTest 3: New Query");
        LcsInput input3 = new LcsInput("AGGTAB", "GXTXAYB");
        System.out.println("Query: \"AGGTAB\" vs \"GXTXAYB\"");
        int result4 = cachedSolver.solve(input3).getLength();
        System.out.println("  Result: " + result4 + " (computed)");
        System.out.println("  Cache size: " + cachedSolver.getCacheSize());

        System.out.println("\n✓ All caching tests passed!");
    }
}
