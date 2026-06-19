/**
 * Comprehensive test suite for LCS implementations.
 */
class LongestCommonSubsequenceTest {

    private LcsSolver solver;

    /**
     * Setup method - initialize with standard solver.
     */
    void setUp() {
        solver = new StandardLcsSolver();
    }

    /**
     * Test base case: both empty strings.
     */
    void testBothEmptyStrings() {
        LcsInput input = new LcsInput("", "");
        LcsResult result = solver.solve(input);
        assert result.getLength() == 0 : "Expected 0 for two empty strings";
    }

    /**
     * Test base case: first string empty.
     */
    void testFirstStringEmpty() {
        LcsInput input = new LcsInput("", "ABC");
        LcsResult result = solver.solve(input);
        assert result.getLength() == 0 : "Expected 0 when first string is empty";
    }

    /**
     * Test base case: second string empty.
     */
    void testSecondStringEmpty() {
        LcsInput input = new LcsInput("ABC", "");
        LcsResult result = solver.solve(input);
        assert result.getLength() == 0 : "Expected 0 when second string is empty";
    }

    /**
     * Test when strings have no common characters.
     */
    void testNoCommonCharacters() {
        LcsInput input = new LcsInput("ABC", "DEF");
        LcsResult result = solver.solve(input);
        assert result.getLength() == 0 : "Expected 0 for no common characters";
    }

    /**
     * Test when strings are identical.
     */
    void testIdenticalStrings() {
        LcsInput input = new LcsInput("ABCDEF", "ABCDEF");
        LcsResult result = solver.solve(input);
        assert result.getLength() == 6 : "Expected 6 for identical strings of length 6";
    }

    /**
     * Test standard example from algorithm literature.
     */
    void testStandardExample() {
        LcsInput input = new LcsInput("AGGTAB", "GXTXAYB");
        LcsResult result = solver.solve(input);
        assert result.getLength() == 5 : "Expected 5 for 'AGGTAB' and 'GXTXAYB'";
    }

    /**
     * Test single character match.
     */
    void testSingleCharacterMatch() {
        LcsInput input = new LcsInput("A", "A");
        LcsResult result = solver.solve(input);
        assert result.getLength() == 1 : "Expected 1 for single matching character";
    }

    /**
     * Test single character no match.
     */
    void testSingleCharacterNoMatch() {
        LcsInput input = new LcsInput("A", "B");
        LcsResult result = solver.solve(input);
        assert result.getLength() == 0 : "Expected 0 for non-matching single characters";
    }

    /**
     * Test symmetry: lcs(A,B) == lcs(B,A).
     */
    void testSymmetry() {
        LcsInput input1 = new LcsInput("ABCDE", "XBYDZ");
        LcsInput input2 = new LcsInput("XBYDZ", "ABCDE");

        int result1 = solver.solve(input1).getLength();
        int result2 = solver.solve(input2).getLength();

        assert result1 == result2 : "Expected symmetry: lcs(A,B) should equal lcs(B,A)";
    }

    /**
     * Test subsequence property: lcs(A,B) <= min(|A|, |B|).
     */
    void testSubsequenceBoundProperty() {
        LcsInput input = new LcsInput("LONGSTRING", "SHORT");
        int result = solver.solve(input).getLength();
        int minLength = Math.min(10, 5);

        assert result <= minLength : "LCS length cannot exceed minimum string length";
    }

    /**
     * Test with longer strings.
     */
    void testLongerStrings() {
        String s1 = "ABCDEFGHIJKLMN";
        String s2 = "ACEGJMNPQRST";
        LcsInput input = new LcsInput(s1, s2);
        LcsResult result = solver.solve(input);
        assert result.getLength() == 8 : "Expected 8 for longer string test case";
    }

    /**
     * Test repeating characters.
     */
    void testRepeatingCharacters() {
        LcsInput input = new LcsInput("AAAA", "AA");
        LcsResult result = solver.solve(input);
        assert result.getLength() == 2 : "Expected 2 for repeating characters";
    }

    /**
     * Test backward-compatible static method.
     */
    void testStaticMethod() {
        int result = LongestCommonSubsequence.lcs("AGGTAB", "GXTXAYB");
        assert result == 5 : "Static method should return 5 for standard example";
    }

    /**
     * Test input validation: null first string.
     */
    void testNullFirstString() {
        try {
            new LcsInput(null, "TEST");
            assert false : "Should throw IllegalArgumentException for null first string";
        } catch (IllegalArgumentException e) {
            assert true : "Correctly threw exception for null input";
        }
    }

    /**
     * Test input validation: null second string.
     */
    void testNullSecondString() {
        try {
            new LcsInput("TEST", null);
            assert false : "Should throw IllegalArgumentException for null second string";
        } catch (IllegalArgumentException e) {
            assert true : "Correctly threw exception for null input";
        }
    }

    /**
     * Test with special characters.
     */
    void testSpecialCharacters() {
        LcsInput input = new LcsInput("A@B#C", "A$B%C");
        LcsResult result = solver.solve(input);
        assert result.getLength() == 3 : "Should handle special characters (A,B,C)";
    }

    /**
     * Test with whitespace.
     */
    void testWhitespace() {
        LcsInput input = new LcsInput("A B C", "ABC");
        LcsResult result = solver.solve(input);
        assert result.getLength() == 3 : "Should handle whitespace in strings";
    }

    /**
     * Test with numbers as strings.
     */
    void testNumericStrings() {
        LcsInput input = new LcsInput("12345", "13579");
        LcsResult result = solver.solve(input);
        assert result.getLength() == 3 : "Expected 3 for numeric strings (1,3,5)";
    }

    /**
     * Run all tests and report results.
     */
    public static void main(String[] args) {
        LongestCommonSubsequenceTest test = new LongestCommonSubsequenceTest();
        int passed = 0;
        int failed = 0;

        // Test methods
        TestCase[] testCases = {
            new TestCase("testBothEmptyStrings", () -> test.setUp(), test::testBothEmptyStrings),
            new TestCase("testFirstStringEmpty", () -> test.setUp(), test::testFirstStringEmpty),
            new TestCase("testSecondStringEmpty", () -> test.setUp(), test::testSecondStringEmpty),
            new TestCase("testNoCommonCharacters", () -> test.setUp(), test::testNoCommonCharacters),
            new TestCase("testIdenticalStrings", () -> test.setUp(), test::testIdenticalStrings),
            new TestCase("testStandardExample", () -> test.setUp(), test::testStandardExample),
            new TestCase("testSingleCharacterMatch", () -> test.setUp(), test::testSingleCharacterMatch),
            new TestCase("testSingleCharacterNoMatch", () -> test.setUp(), test::testSingleCharacterNoMatch),
            new TestCase("testSymmetry", () -> test.setUp(), test::testSymmetry),
            new TestCase("testSubsequenceBoundProperty", () -> test.setUp(), test::testSubsequenceBoundProperty),
            new TestCase("testLongerStrings", () -> test.setUp(), test::testLongerStrings),
            new TestCase("testRepeatingCharacters", () -> test.setUp(), test::testRepeatingCharacters),
            new TestCase("testStaticMethod", () -> test.setUp(), test::testStaticMethod),
            new TestCase("testNullFirstString", () -> test.setUp(), test::testNullFirstString),
            new TestCase("testNullSecondString", () -> test.setUp(), test::testNullSecondString),
            new TestCase("testSpecialCharacters", () -> test.setUp(), test::testSpecialCharacters),
            new TestCase("testWhitespace", () -> test.setUp(), test::testWhitespace),
            new TestCase("testNumericStrings", () -> test.setUp(), test::testNumericStrings),
        };

        System.out.println("=== LCS Test Suite ===\n");

        for (TestCase testCase : testCases) {
            try {
                testCase.setUp();
                testCase.runTest();
                System.out.println("✓ " + testCase.getName());
                passed++;
            } catch (AssertionError e) {
                System.out.println("✗ " + testCase.getName() + " - " + e.getMessage());
                failed++;
            } catch (Exception e) {
                System.out.println("✗ " + testCase.getName() + " - " + e.getClass().getName() + ": " + e.getMessage());
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

    /**
     * Helper class for test case management.
     */
    static class TestCase {
        private final String name;
        private final Runnable setUp;
        private final Runnable test;

        TestCase(String name, Runnable setUp, Runnable test) {
            this.name = name;
            this.setUp = setUp;
            this.test = test;
        }

        String getName() {
            return name;
        }

        void setUp() {
            setUp.run();
        }

        void runTest() {
            test.run();
        }
    }
}
