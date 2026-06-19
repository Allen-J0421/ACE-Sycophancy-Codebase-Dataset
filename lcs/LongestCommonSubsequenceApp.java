package lcs;

/**
 * Command-line entry point for the longest common subsequence example.
 */
public final class LongestCommonSubsequenceApp {
    private static final String USAGE = "Usage: java lcs.LongestCommonSubsequenceApp <first> <second>";

    private LongestCommonSubsequenceApp() {
        // Utility class.
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            runSample();
            return;
        }

        if (args.length == 2) {
            printResult(args[0], args[1]);
            return;
        }

        System.err.println(USAGE);
        System.exit(1);
    }

    private static void runSample() {
        printResult("AGGTAB", "GXTXAYB");
    }

    private static void printResult(String first, String second) {
        LcsResult result = LongestCommonSubsequence.analyze(first, second);
        System.out.println(result.length());
    }
}
