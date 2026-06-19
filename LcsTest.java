/**
 * Modernized test suite using assertions and parametrized approach.
 * Replaces custom test runner with standard testing patterns.
 * Tests all core functionality across different solver implementations.
 */
class LcsTest {

    private LcsSolver standardSolver;
    private LcsSolver spaceSolver;
    private LcsSolver cachedSolver;

    /**
     * Setup - initialize solvers before each test group.
     */
    void setUp() {
        standardSolver = new StandardLcsSolver();
        spaceSolver = new SpaceOptimizedLcsSolver();
        cachedSolver = new CachedLcsSolver(new StandardLcsSolver());
    }

    /**
     * Test that all implementations produce identical results.
     */
    void testAllImplementationsMatch() {
        TestData.TestCase[] cases = TestData.toTestCases(TestData.STANDARD_CASES);

        for (TestData.TestCase testCase : cases) {
            LcsInput input = new LcsInput(testCase.s1, testCase.s2);

            int standard = standardSolver.solve(input).getLength();
            int optimized = spaceSolver.solve(input).getLength();
            int cached = cachedSolver.solve(input).getLength();

            assert standard == testCase.expectedLength :
                    String.format("Standard failed for %s", testCase);
            assert optimized == testCase.expectedLength :
                    String.format("Space-optimized failed for %s", testCase);
            assert cached == testCase.expectedLength :
                    String.format("Cached failed for %s", testCase);
            assert standard == optimized && optimized == cached :
                    String.format("Implementations diverged for %s", testCase);
        }
    }

    /**
     * Test edge cases (empty strings, single chars, etc).
     */
    void testEdgeCases() {
        TestData.TestCase[] cases = TestData.toTestCases(TestData.EDGE_CASES);

        for (TestData.TestCase testCase : cases) {
            LcsInput input = new LcsInput(testCase.s1, testCase.s2);
            int result = standardSolver.solve(input).getLength();

            assert result == testCase.expectedLength :
                    String.format("Edge case failed: %s (expected %d, got %d)",
                            testCase, testCase.expectedLength, result);
        }
    }

    /**
     * Test special characters, unicode, etc.
     */
    void testSpecialCases() {
        TestData.TestCase[] cases = TestData.toTestCases(TestData.SPECIAL_CASES);

        for (TestData.TestCase testCase : cases) {
            LcsInput input = new LcsInput(testCase.s1, testCase.s2);
            int result = standardSolver.solve(input).getLength();

            assert result == testCase.expectedLength :
                    String.format("Special case failed: %s (expected %d, got %d)",
                            testCase, testCase.expectedLength, result);
        }
    }

    /**
     * Test symmetry property: lcs(A,B) == lcs(B,A).
     */
    void testSymmetryProperty() {
        TestData.TestCase[] cases = TestData.toTestCases(TestData.STANDARD_CASES);

        for (TestData.TestCase testCase : cases) {
            LcsInput input1 = new LcsInput(testCase.s1, testCase.s2);
            LcsInput input2 = new LcsInput(testCase.s2, testCase.s1);

            int result1 = standardSolver.solve(input1).getLength();
            int result2 = standardSolver.solve(input2).getLength();

            assert result1 == result2 :
                    String.format("Symmetry failed: lcs(%s,%s)=%d but lcs(%s,%s)=%d",
                            testCase.s1, testCase.s2, result1,
                            testCase.s2, testCase.s1, result2);
        }
    }

    /**
     * Test bounds property: lcs(A,B) <= min(len(A), len(B)).
     */
    void testBoundsProperty() {
        TestData.TestCase[] cases = TestData.toTestCases(TestData.STANDARD_CASES);

        for (TestData.TestCase testCase : cases) {
            LcsInput input = new LcsInput(testCase.s1, testCase.s2);
            int result = standardSolver.solve(input).getLength();
            int minLength = Math.min(testCase.s1.length(), testCase.s2.length());

            assert result <= minLength :
                    String.format("Bounds violated: lcs(%s,%s)=%d but min_length=%d",
                            testCase.s1, testCase.s2, result, minLength);
        }
    }

    /**
     * Test null input validation.
     */
    void testNullInputs() {
        try {
            new LcsInput(null, "test");
            assert false : "Should throw on null first string";
        } catch (IllegalArgumentException e) {
            assert e.getMessage().contains("First") : "Error message should identify first string";
        }

        try {
            new LcsInput("test", null);
            assert false : "Should throw on null second string";
        } catch (IllegalArgumentException e) {
            assert e.getMessage().contains("Second") : "Error message should identify second string";
        }
    }

    /**
     * Test sequence reconstruction.
     */
    void testSequenceReconstruction() {
        for (TestData.ReconstructionCase testCase : TestData.RECONSTRUCTION_CASES) {
            String lcs = LcsSequenceReconstructor.reconstructLcs(testCase.s1, testCase.s2);

            assert lcs.equals(testCase.expectedLcs) :
                    String.format("Reconstruction failed: lcs(%s,%s)='%s' but expected '%s'",
                            testCase.s1, testCase.s2, lcs, testCase.expectedLcs);
        }
    }

    /**
     * Test that reconstruction length matches computed length.
     */
    void testReconstructionLengthMatches() {
        for (TestData.ReconstructionCase testCase : TestData.RECONSTRUCTION_CASES) {
            LcsInput input = new LcsInput(testCase.s1, testCase.s2);
            int computedLength = standardSolver.solve(input).getLength();
            String reconstructed = LcsSequenceReconstructor.reconstructLcs(testCase.s1, testCase.s2);

            assert reconstructed.length() == computedLength :
                    String.format("Length mismatch for %s: computed=%d, reconstructed length=%d",
                            testCase.s1, computedLength, reconstructed.length());
        }
    }

    /**
     * Test caching behavior.
     */
    void testCachingBehavior() {
        CachedLcsSolver cached = new CachedLcsSolver(new StandardLcsSolver());

        LcsInput input1 = new LcsInput("ABCD", "BCDE");
        LcsInput input2 = new LcsInput("BCDE", "ABCD"); // Reversed args

        // First call populates cache
        int result1 = cached.solve(input1).getLength();
        int cacheSize1 = cached.getCacheSize();

        // Same pair (different order) should hit cache
        int result2 = cached.solve(input2).getLength();
        int cacheSize2 = cached.getCacheSize();

        assert cacheSize1 == 1 : "Cache should have 1 entry after first call";
        assert cacheSize2 == 1 : "Cache size should remain 1 for reversed arguments";
        assert result1 == result2 : "Results should match regardless of argument order";
    }

    /**
     * Test backward compatibility with static method.
     */
    void testStaticMethodBackwardCompat() {
        int result = LongestCommonSubsequence.lcs("AGGTAB", "GXTXAYB");
        assert result == 4 : "Static method should return correct LCS length";
    }

    /**
     * Run all tests and report results.
     */
    public static void main(String[] args) {
        LcsTest test = new LcsTest();
        int passed = 0;
        int failed = 0;

        String[] testMethods = {
                "testAllImplementationsMatch",
                "testEdgeCases",
                "testSpecialCases",
                "testSymmetryProperty",
                "testBoundsProperty",
                "testNullInputs",
                "testSequenceReconstruction",
                "testReconstructionLengthMatches",
                "testCachingBehavior",
                "testStaticMethodBackwardCompat",
        };

        System.out.println("=== LCS Test Suite (Modernized) ===\n");

        for (String methodName : testMethods) {
            try {
                test.setUp();
                java.lang.reflect.Method method = LcsTest.class.getDeclaredMethod(methodName);
                method.invoke(test);

                System.out.println("✓ " + methodName);
                passed++;
            } catch (AssertionError e) {
                System.out.println("✗ " + methodName + " - " + e.getMessage());
                failed++;
            } catch (Exception e) {
                System.out.println("✗ " + methodName + " - " + e.getClass().getSimpleName() +
                        ": " + e.getMessage());
                failed++;
            }
        }

        System.out.println("\n=== Results ===");
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);
        System.out.println("Total: " + (passed + failed));

        if (failed > 0) {
            System.exit(1);
        }
    }
}
