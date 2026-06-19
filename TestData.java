/**
 * Shared test data for LCS solver testing.
 * Centralizes test case definitions to avoid duplication across test classes
 * and enable consistent testing of all solver implementations.
 */
class TestData {

    /**
     * Standard test cases covering common scenarios.
     * Format: {string1, string2, expected_lcs_length}
     */
    static final Object[][] STANDARD_CASES = {
        {"AGGTAB", "GXTXAYB", 4},
        {"", "", 0},
        {"ABC", "DEF", 0},
        {"ABCDEF", "ABCDEF", 6},
        {"AAAA", "AA", 2},
        {"ABCDE", "XBYDZ", 2},
        {"LONGSTRING", "SHORT", 2},
        {"A", "A", 1},
        {"A", "B", 0},
    };

    /**
     * Edge cases covering boundary conditions.
     * Format: {string1, string2, expected_lcs_length}
     */
    static final Object[][] EDGE_CASES = {
        {"", "", 0},                    // Both empty
        {"", "ABC", 0},                 // First empty
        {"ABC", "", 0},                 // Second empty
        {"A", "A", 1},                  // Single matching char
        {"A", "B", 0},                  // Single non-matching
    };

    /**
     * Special character test cases.
     * Format: {string1, string2, expected_lcs_length}
     */
    static final Object[][] SPECIAL_CASES = {
        {"A@B#C", "A$B%C", 3},          // Special chars (A,B,C match)
        {"A B C", "ABC", 3},            // Whitespace (A,B,C match)
        {"12345", "13579", 3},          // Numbers (1,3,5 match)
        {"café", "cáfé", 4},            // Accented characters
    };

    /**
     * Test case object combining input strings and expected result.
     */
    static class TestCase {
        final String s1;
        final String s2;
        final int expectedLength;

        TestCase(String s1, String s2, int expectedLength) {
            this.s1 = s1;
            this.s2 = s2;
            this.expectedLength = expectedLength;
        }

        @Override
        public String toString() {
            return String.format("TestCase(\"%s\", \"%s\", expect %d)",
                    s1, s2, expectedLength);
        }
    }

    /**
     * Converts raw test data arrays to TestCase objects.
     *
     * @param rawData array of {string1, string2, expectedLength} arrays
     * @return TestCase objects
     */
    static TestCase[] toTestCases(Object[][] rawData) {
        TestCase[] cases = new TestCase[rawData.length];
        for (int i = 0; i < rawData.length; i++) {
            cases[i] = new TestCase(
                    (String) rawData[i][0],
                    (String) rawData[i][1],
                    (int) rawData[i][2]
            );
        }
        return cases;
    }

    /**
     * Test cases for reconstruction (expecting specific LCS strings).
     * Format: {string1, string2, expected_lcs_string}
     */
    static class ReconstructionCase {
        final String s1;
        final String s2;
        final String expectedLcs;

        ReconstructionCase(String s1, String s2, String expectedLcs) {
            this.s1 = s1;
            this.s2 = s2;
            this.expectedLcs = expectedLcs;
        }

        @Override
        public String toString() {
            return String.format("ReconstructionCase(\"%s\", \"%s\", expect \"%s\")",
                    s1, s2, expectedLcs);
        }
    }

    static final ReconstructionCase[] RECONSTRUCTION_CASES = {
        new ReconstructionCase("AGGTAB", "GXTXAYB", "GTAB"),
        new ReconstructionCase("", "", ""),
        new ReconstructionCase("ABC", "DEF", ""),
        new ReconstructionCase("ABCDEF", "ABCDEF", "ABCDEF"),
        new ReconstructionCase("AAAA", "AA", "AA"),
        new ReconstructionCase("ABCDE", "XBYDZ", "BD"),
    };

    /**
     * Performance test cases with larger strings.
     */
    static class PerformanceCase {
        final String s1;
        final String s2;
        final int maxExpectedTimeMs;

        PerformanceCase(String s1, String s2, int maxExpectedTimeMs) {
            this.s1 = s1;
            this.s2 = s2;
            this.maxExpectedTimeMs = maxExpectedTimeMs;
        }
    }

    static final PerformanceCase[] PERFORMANCE_CASES = {
        new PerformanceCase(generateString('A', 100), generateString('B', 100), 10),
        new PerformanceCase(generateString('A', 500), generateString('B', 500), 100),
    };

    private static String generateString(char c, int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(c);
        }
        return sb.toString();
    }
}
