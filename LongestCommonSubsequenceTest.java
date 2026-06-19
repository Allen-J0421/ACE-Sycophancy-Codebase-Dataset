public class LongestCommonSubsequenceTest {

    private static final LcsSolver solver = new LongestCommonSubsequence();
    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        testBasicCase();
        testBothEmpty();
        testOneEmpty();
        testNoCommonCharacters();
        testIdenticalStrings();
        testSingleMatchingChar();
        testSingleNonMatchingChar();

        System.out.printf("%d passed, %d failed%n", passed, failed);
        if (failed > 0) System.exit(1);
    }

    private static void check(String label, String s1, String s2, int expectedLength, String expectedSubseq) {
        LcsResult result = solver.compute(new LcsInput(s1, s2));
        boolean ok = result.getLength() == expectedLength && result.getSubsequence().equals(expectedSubseq);
        if (ok) {
            passed++;
        } else {
            failed++;
            System.err.printf("FAIL [%s]: lcs(\"%s\", \"%s\") => length=%d subseq=\"%s\", want length=%d subseq=\"%s\"%n",
                label, s1, s2, result.getLength(), result.getSubsequence(), expectedLength, expectedSubseq);
        }
    }

    private static void testBasicCase() {
        check("basic", "AGGTAB", "GXTXAYB", 4, "GTAB");
    }

    private static void testBothEmpty() {
        check("both empty", "", "", 0, "");
    }

    private static void testOneEmpty() {
        check("first empty", "", "ABC", 0, "");
        check("second empty", "ABC", "", 0, "");
    }

    private static void testNoCommonCharacters() {
        check("no common chars", "ABC", "DEF", 0, "");
    }

    private static void testIdenticalStrings() {
        check("identical", "ABCD", "ABCD", 4, "ABCD");
    }

    private static void testSingleMatchingChar() {
        check("single match", "A", "A", 1, "A");
    }

    private static void testSingleNonMatchingChar() {
        check("single no match", "A", "B", 0, "");
    }
}
