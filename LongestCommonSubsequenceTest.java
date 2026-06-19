public final class LongestCommonSubsequenceTest {
    private static final TestCase[] TEST_CASES = {
        new TestCase("AGGTAB", "GXTXAYB", 4),
        new TestCase("", "abc", 0),
        new TestCase("abc", "", 0),
        new TestCase("abc", "abc", 3),
        new TestCase("abc", "def", 0),
        new TestCase("ABCDGH", "AEDFHR", 3),
        new TestCase("XMJYAUZ", "MZJAWXU", 4)
    };

    private LongestCommonSubsequenceTest() {
    }

    public static void main(String[] args) {
        for (TestCase testCase : TEST_CASES) {
            assertLcs(testCase);
        }

        assertSymmetric("abcde", "ace");
        assertSymmetric("XMJYAUZ", "MZJAWXU");
        assertNullRejected();

        System.out.println("All tests passed.");
    }

    private static void assertLcs(TestCase testCase) {
        int actual = LongestCommonSubsequence.lcs(testCase.first(), testCase.second());

        if (actual != testCase.expected()) {
            throw new AssertionError(
                "Expected LCS length %d for \"%s\" and \"%s\" but got %d"
                    .formatted(testCase.expected(), testCase.first(), testCase.second(), actual)
            );
        }
    }

    private static void assertSymmetric(String first, String second) {
        int forward = LongestCommonSubsequence.lcs(first, second);
        int reverse = LongestCommonSubsequence.lcs(second, first);

        if (forward != reverse) {
            throw new AssertionError(
                "Expected symmetry for \"%s\" and \"%s\" but got %d and %d"
                    .formatted(first, second, forward, reverse)
            );
        }
    }

    private static void assertNullRejected() {
        assertNullRejected(null, "abc", "first must not be null");
        assertNullRejected("abc", null, "second must not be null");
    }

    private static void assertNullRejected(String first, String second, String message) {
        try {
            LongestCommonSubsequence.lcs(first, second);
            throw new AssertionError("Expected NullPointerException");
        } catch (NullPointerException exception) {
            if (!message.equals(exception.getMessage())) {
                throw new AssertionError(
                    "Expected message \"%s\" but got \"%s\""
                        .formatted(message, exception.getMessage())
                );
            }
        }
    }

    private record TestCase(String first, String second, int expected) {
    }
}
