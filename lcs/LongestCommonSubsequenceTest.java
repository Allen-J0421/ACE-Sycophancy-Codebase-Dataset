package lcs;

public final class LongestCommonSubsequenceTest {
    private LongestCommonSubsequenceTest() {
        // Utility class.
    }

    public static void main(String[] args) {
        assertThrows(null, "ABC", NullPointerException.class);
        assertThrows("ABC", null, NullPointerException.class);

        assertLength("", "", 0);
        assertLength("", "ABC", 0);
        assertLength("ABC", "", 0);
        assertLength(new StringBuilder("ABC"), new StringBuilder("AC"), 2);
        assertLength("ABC", "ABC", 3);
        assertLength("ABCDEF", "FBDAMN", 2);
        assertLength("AGGTAB", "GXTXAYB", 4);
        assertLength("XMJYAUZ", "MZJAWXU", 4);
        assertLength("AAAA", "AA", 2);
        assertLength("ABCBDAB", "BDCABA", 4);
        assertLength("HELLO", "YELLOW", 4);

        assertAnalysis("", "ABC", 0);
        assertAnalysis("AGGTAB", "GXTXAYB", 4);
        assertAnalysis("XMJYAUZ", "MZJAWXU", 4);
        assertAnalysis("ABCBDAB", "BDCABA", 4);

        System.out.println("All LCS tests passed.");
    }

    private static void assertThrows(String first, String second, Class<? extends Throwable> expected) {
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

    private static void assertLength(CharSequence first, CharSequence second, int expected) {
        int actual = LongestCommonSubsequence.lcs(first, second);
        if (actual != expected) {
            throw new AssertionError(
                    "Expected LCS(" + first + ", " + second + ") = " + expected + ", but got " + actual);
        }
    }

    private static void assertAnalysis(CharSequence first, CharSequence second, int expectedLength) {
        LcsResult result = LongestCommonSubsequence.analyze(first, second);
        if (result.length() != expectedLength) {
            throw new AssertionError(
                    "Expected analysis length " + expectedLength + " but got " + result.length());
        }

        if (result.subsequence().length() != expectedLength) {
            throw new AssertionError(
                    "Expected subsequence length " + expectedLength + " but got " + result.subsequence().length());
        }

        if (!isSubsequence(result.subsequence(), first) || !isSubsequence(result.subsequence(), second)) {
            throw new AssertionError("Analysis subsequence is not valid: " + result.subsequence());
        }
    }

    private static boolean isSubsequence(CharSequence candidate, CharSequence value) {
        int candidateIndex = 0;
        for (int i = 0; i < value.length() && candidateIndex < candidate.length(); i++) {
            if (value.charAt(i) == candidate.charAt(candidateIndex)) {
                candidateIndex++;
            }
        }
        return candidateIndex == candidate.length();
    }
}
