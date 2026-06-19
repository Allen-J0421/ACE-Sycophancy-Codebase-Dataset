public final class LongestCommonSubsequenceCli {
    private static final String SAMPLE_FIRST = "AGGTAB";
    private static final String SAMPLE_SECOND = "GXTXAYB";
    private static final int REQUIRED_ARGUMENT_COUNT = 2;

    private LongestCommonSubsequenceCli() {
    }

    public static void main(String[] args) {
        String[] inputs = args.length == REQUIRED_ARGUMENT_COUNT
            ? args
            : new String[] {SAMPLE_FIRST, SAMPLE_SECOND};

        System.out.println(LongestCommonSubsequence.lcs(inputs[0], inputs[1]));
    }
}
