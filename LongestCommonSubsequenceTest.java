public final class LongestCommonSubsequenceTest {
    private LongestCommonSubsequenceTest() {
        // Utility class.
    }

    public static void main(String[] args) {
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

    private static void assertLcs(String first, String second, int expected) {
        int actual = LongestCommonSubsequence.lcs(first, second);
        if (actual != expected) {
            throw new AssertionError(
                    "Expected LCS(" + first + ", " + second + ") = " + expected + ", but got " + actual);
        }
    }
}
