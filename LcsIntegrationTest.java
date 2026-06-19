/**
 * Integration tests for complete LCS workflows.
 * Tests the full pipeline from input validation through result reconstruction.
 */
class LcsIntegrationTest {

    /**
     * Test complete workflow with standard example.
     * Validates: Input → Solver → Result → Reconstruction
     */
    void testCompleteWorkflow() {
        // 1. Setup with standard example
        String s1 = TestData.EXAMPLE_S1;
        String s2 = TestData.EXAMPLE_S2;

        // 2. Validate input
        LcsInput input = new LcsInput(s1, s2);
        assert input.getFirstString().equals(s1);
        assert input.getSecondString().equals(s2);

        // 3. Compute length with standard solver
        LcsSolver solver = new StandardLcsSolver();
        LcsResult result = solver.solve(input);
        int lcsLength = result.getLength();

        // 4. Reconstruct sequence
        String lcsSequence = LcsSequenceReconstructor.reconstructLcs(s1, s2);

        // 5. Verify consistency
        assert lcsSequence.length() == lcsLength :
                "Reconstruction length (" + lcsSequence.length() +
                        ") should equal computed length (" + lcsLength + ")";

        // 6. Verify it's a valid common subsequence
        TestAssertions.assertCommonSubsequence(lcsSequence, s1, s2);
    }

    /**
     * Test that all solver implementations agree on same input.
     */
    void testAllSolversAgree() {
        LcsInput input = new LcsInput(TestData.EXAMPLE_S1, TestData.EXAMPLE_S2);

        LcsSolver standard = new StandardLcsSolver();
        LcsSolver optimized = new SpaceOptimizedLcsSolver();
        LcsSolver cached = new CachedLcsSolver(new StandardLcsSolver());

        int result1 = standard.solve(input).getLength();
        int result2 = optimized.solve(input).getLength();
        int result3 = cached.solve(input).getLength();

        assert result1 == result2 && result2 == result3 :
                "All solvers should return same result";
        assert result1 == TestData.EXAMPLE_LCS_LENGTH :
                "Should match expected length";
    }

    /**
     * Test workflow with edge case: identical strings.
     */
    void testIdenticalStringsWorkflow() {
        String s = TestData.IDENTICAL_STRING;

        LcsInput input = new LcsInput(s, s);
        LcsSolver solver = new StandardLcsSolver();
        LcsResult result = solver.solve(input);
        String lcs = LcsSequenceReconstructor.reconstructLcs(s, s);

        assert result.getLength() == s.length() :
                "LCS of identical strings should equal string length";
        assert lcs.equals(s) :
                "LCS sequence should equal the original string";
    }

    /**
     * Test workflow with edge case: no common characters.
     */
    void testNoCommonCharactersWorkflow() {
        String s1 = TestData.NO_MATCH_S1;
        String s2 = TestData.NO_MATCH_S2;

        LcsInput input = new LcsInput(s1, s2);
        LcsSolver solver = new StandardLcsSolver();
        LcsResult result = solver.solve(input);
        String lcs = LcsSequenceReconstructor.reconstructLcs(s1, s2);

        assert result.getLength() == 0 :
                "LCS of completely different strings should be 0";
        assert lcs.equals("") :
                "LCS sequence should be empty string";
    }

    /**
     * Test caching workflow: repeated queries use cache.
     */
    void testCachingWorkflow() {
        LcsInput input1 = new LcsInput(TestData.EXAMPLE_S1, TestData.EXAMPLE_S2);
        LcsInput input2 = new LcsInput(TestData.EXAMPLE_S2, TestData.EXAMPLE_S1); // reversed

        CachedLcsSolver cached = new CachedLcsSolver(new StandardLcsSolver());

        // First query
        int result1 = cached.solve(input1).getLength();
        int cache1Size = cached.getCacheSize();

        // Repeated query (different order, should use cache)
        int result2 = cached.solve(input2).getLength();
        int cache2Size = cached.getCacheSize();

        assert result1 == result2 : "Results should be same regardless of order";
        assert cache1Size == 1 : "Should have 1 cache entry after first call";
        assert cache2Size == 1 : "Cache size should remain 1 (symmetric)";
    }

    /**
     * Test error handling workflow: null inputs.
     */
    void testNullInputHandling() {
        try {
            new LcsInput(null, "test");
            assert false : "Should throw on null first string";
        } catch (IllegalArgumentException e) {
            assert e.getMessage().contains("First") :
                    "Error should identify first string";
        }

        try {
            new LcsInput("test", null);
            assert false : "Should throw on null second string";
        } catch (IllegalArgumentException e) {
            assert e.getMessage().contains("Second") :
                    "Error should identify second string";
        }
    }

    /**
     * Test with simple case for easy verification.
     */
    void testSimpleCaseWorkflow() {
        String s1 = TestData.SIMPLE_CASE_S1;
        String s2 = TestData.SIMPLE_CASE_S2;

        LcsInput input = new LcsInput(s1, s2);
        int length = new StandardLcsSolver().solve(input).getLength();
        String sequence = LcsSequenceReconstructor.reconstructLcs(s1, s2);

        assert length == TestData.SIMPLE_CASE_LCS_LENGTH :
                "Expected length " + TestData.SIMPLE_CASE_LCS_LENGTH + ", got " + length;
        assert sequence.equals(TestData.SIMPLE_CASE_LCS_SEQUENCE) ||
                sequence.length() == TestData.SIMPLE_CASE_LCS_LENGTH :
                "Sequence should have expected length";
    }

    /**
     * Test property: LCS length never exceeds shorter string.
     */
    void testLcsLengthBounds() {
        String[] pairs = {
                TestData.EXAMPLE_S1, TestData.EXAMPLE_S2,
                TestData.SIMPLE_CASE_S1, TestData.SIMPLE_CASE_S2,
                TestData.IDENTICAL_STRING, TestData.IDENTICAL_STRING,
                TestData.NO_MATCH_S1, TestData.NO_MATCH_S2,
        };

        for (int i = 0; i < pairs.length; i += 2) {
            String s1 = pairs[i];
            String s2 = pairs[i + 1];

            LcsInput input = new LcsInput(s1, s2);
            int length = new StandardLcsSolver().solve(input).getLength();

            TestAssertions.assertValidLcsLength(length, s1.length(), s2.length());
        }
    }

    /**
     * Test property: LCS is symmetric (order doesn't matter).
     */
    void testSymmetryProperty() {
        String[] pairs = {
                TestData.EXAMPLE_S1, TestData.EXAMPLE_S2,
                TestData.SIMPLE_CASE_S1, TestData.SIMPLE_CASE_S2,
                "ABC", "DEF",
        };

        LcsSolver solver = new StandardLcsSolver();

        for (int i = 0; i < pairs.length; i += 2) {
            String s1 = pairs[i];
            String s2 = pairs[i + 1];

            LcsInput input1 = new LcsInput(s1, s2);
            LcsInput input2 = new LcsInput(s2, s1);

            int result1 = solver.solve(input1).getLength();
            int result2 = solver.solve(input2).getLength();

            TestAssertions.assertSymmetric(result1, result2, s1, s2);
        }
    }

    /**
     * Run all integration tests and report results.
     */
    public static void main(String[] args) {
        LcsIntegrationTest test = new LcsIntegrationTest();
        int passed = 0;
        int failed = 0;

        String[] testMethods = {
                "testCompleteWorkflow",
                "testAllSolversAgree",
                "testIdenticalStringsWorkflow",
                "testNoCommonCharactersWorkflow",
                "testCachingWorkflow",
                "testNullInputHandling",
                "testSimpleCaseWorkflow",
                "testLcsLengthBounds",
                "testSymmetryProperty",
        };

        System.out.println("=== LCS Integration Test Suite ===\n");

        for (String methodName : testMethods) {
            try {
                java.lang.reflect.Method method = LcsIntegrationTest.class.getDeclaredMethod(methodName);
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
