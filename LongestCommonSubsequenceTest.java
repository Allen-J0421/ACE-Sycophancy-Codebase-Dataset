public final class LongestCommonSubsequenceTest {
    private LongestCommonSubsequenceTest() {
        // Utility class.
    }

    public static void main(String[] args) {
        assertLcs(null, "ABC", NullPointerException.class);
        assertLcs("ABC", null, NullPointerException.class);
        assertLcs("", "", 0);
        assertLcs("", "ABC", 0);
        assertLcs("ABC", "", 0);
        assertLcs("ABC", "ABC", 3);
        assertLcs("ABCDEF", "FBDAMN", 2);
        assertLcs("AGGTAB", "GXTXAYB", 4);
        assertLcs("XMJYAUZ", "MZJAWXU", 4);
        assertLcs("AAAA", "AA", 2);
        assertLcs("ABCBDAB", "BDCABA", 4);
        assertLcs("HELLO", "YELLOW", 4);

        System.out.println("All LCS tests passed.");
    }

    private static void assertLcs(String first, String second, Class<? extends Throwable> expected) {
        try {
            LongestCommonSubsequence.lcs(first, second);
            throw new AssertionError("Expected " + expected.getSimpleName() + " for LCS(" + first + ", " + second + ")");
        } catch (Throwable actual) {
            if (!expected.isInstance(actual)) {
                throw new AssertionError(
                        "Expected " + expected.getSimpleName() + " but got " + actual.getClass().getSimpleName(),
                        actual);
            }
        }
    }

    private static void assertLcs(String first, String second, int expected) {
        int actual = LongestCommonSubsequence.lcs(first, second);
        if (actual != expected) {
            throw new AssertionError(
                    "Expected LCS(" + first + ", " + second + ") = " + expected + ", but got " + actual);
        }
    }
}
