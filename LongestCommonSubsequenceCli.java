/**
 * Command-line interface for LCS computation.
 */
class LongestCommonSubsequenceCli {
    private static final LcsSolver solver = new StandardLcsSolver();

    // Configuration constants
    private static final String USAGE_MESSAGE =
            "Usage: java LongestCommonSubsequenceCli [string1] [string2]";
    private static final String DEFAULT_ARGS_MESSAGE =
            "Running with default examples (no arguments provided).";
    private static final String INVALID_ARGS_MESSAGE =
            "Invalid arguments. Expected 0 or 2 arguments.";

    /**
     * Main entry point for CLI.
     * Usage: java LongestCommonSubsequenceCli [string1] [string2]
     * If no arguments provided, runs with example data.
     *
     * @param args command line arguments (optional: [string1] [string2])
     */
    public static void main(String[] args) {
        String s1;
        String s2;

        if (args.length == 2) {
            s1 = args[0];
            s2 = args[1];
        } else if (args.length == 0) {
            // Use default examples from TestData
            s1 = TestData.EXAMPLE_S1;
            s2 = TestData.EXAMPLE_S2;
            System.out.println(DEFAULT_ARGS_MESSAGE);
            System.out.println(USAGE_MESSAGE + "\n");
        } else {
            System.err.println(INVALID_ARGS_MESSAGE);
            System.err.println(USAGE_MESSAGE);
            System.exit(1);
            return;
        }

        try {
            LcsInput input = new LcsInput(s1, s2);
            LcsResult result = solver.solve(input);

            // Output results
            printResult(s1, s2, result);
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Prints LCS computation result in a formatted manner.
     *
     * @param s1     first input string
     * @param s2     second input string
     * @param result computation result
     */
    private static void printResult(String s1, String s2, LcsResult result) {
        System.out.println("String 1: \"" + s1 + "\"");
        System.out.println("String 2: \"" + s2 + "\"");
        System.out.println("LCS Length: " + result.getLength());
    }
}
