/**
 * Command-line interface for LCS computation.
 */
class LongestCommonSubsequenceCli {
    private static final LcsSolver solver = new StandardLcsSolver();

    /**
     * Main entry point for CLI.
     * Usage: java LongestCommonSubsequenceCli [string1] [string2]
     * If no arguments provided, runs with example data.
     *
     * @param args command line arguments (optional)
     */
    public static void main(String[] args) {
        String s1;
        String s2;

        if (args.length == 2) {
            s1 = args[0];
            s2 = args[1];
        } else if (args.length == 0) {
            s1 = "AGGTAB";
            s2 = "GXTXAYB";
            System.out.println("Running with default examples (no arguments provided).");
            System.out.println("Usage: java LongestCommonSubsequenceCli <string1> <string2>\n");
        } else {
            System.err.println("Invalid arguments. Expected 0 or 2 arguments.");
            System.err.println("Usage: java LongestCommonSubsequenceCli [string1] [string2]");
            System.exit(1);
            return;
        }

        try {
            LcsInput input = new LcsInput(s1, s2);
            LcsResult result = solver.solve(input);

            System.out.println("String 1: \"" + s1 + "\"");
            System.out.println("String 2: \"" + s2 + "\"");
            System.out.println("LCS Length: " + result.getLength());
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }
}
