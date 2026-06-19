public final class LongestCommonSubsequenceTest {
    private LongestCommonSubsequenceTest() {
    }

    public static void main(String[] args) {
        assertLcs("AGGTAB", "GXTXAYB", 4);
        assertLcs("", "abc", 0);
        assertLcs("abc", "", 0);
        assertLcs("abc", "abc", 3);
        assertLcs("abc", "def", 0);
        assertLcs("ABCDGH", "AEDFHR", 3);
        assertLcs("XMJYAUZ", "MZJAWXU", 4);
        assertSymmetric("abcde", "ace");
        assertNullRejected();

        System.out.println("All tests passed.");
    }

    private static void assertLcs(String first, String second, int expected) {
        int actual = LongestCommonSubsequence.lcs(first, second);

        if (actual != expected) {
            throw new AssertionError(
                "Expected LCS length %d for \"%s\" and \"%s\" but got %d"
                    .formatted(expected, first, second, actual)
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
}
