/**
 * Command-line entry point for the longest common subsequence example.
 */
public final class LongestCommonSubsequenceApp {
    private static final String USAGE = "Usage: java LongestCommonSubsequenceApp <first> <second>";

    private LongestCommonSubsequenceApp() {
        // Utility class.
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            runSample();
            return;
        }

        if (args.length == 2) {
            System.out.println(LongestCommonSubsequence.lcs(args[0], args[1]));
            return;
        }

        System.err.println(USAGE);
        System.exit(1);
    }

    private static void runSample() {
        System.out.println(LongestCommonSubsequence.lcs("AGGTAB", "GXTXAYB"));
    }
}
