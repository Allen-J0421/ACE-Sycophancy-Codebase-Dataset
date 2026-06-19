/**
 * Command-line interface for LCS computation.
 * Supports multiple output formats via ResultFormatter strategy.
 */
class LongestCommonSubsequenceCli {

    // Configuration constants
    private static final String USAGE_MESSAGE =
            "Usage: java LongestCommonSubsequenceCli [--format=FORMAT] [string1] [string2]";
    private static final String DEFAULT_ARGS_MESSAGE =
            "Running with default examples (no arguments provided).";
    private static final String INVALID_ARGS_MESSAGE =
            "Invalid arguments. Expected 0 or 2-3 arguments.";

    /**
     * Output format enumeration.
     */
    enum OutputFormat {
        SIMPLE("simple", new SimpleResultFormatter()),
        DETAILED("detailed", new DetailedResultFormatter()),
        JSON("json", new JsonResultFormatter()),
        CSV("csv", new CsvResultFormatter()),
        COMPACT("compact", new CompactResultFormatter());

        final String name;
        final ResultFormatter formatter;

        OutputFormat(String name, ResultFormatter formatter) {
            this.name = name;
            this.formatter = formatter;
        }

        /**
         * Gets formatter for the specified format name.
         *
         * @param formatName format name (e.g., "simple", "json")
         * @return formatter for that format
         */
        static ResultFormatter getFormatter(String formatName) {
            for (OutputFormat format : OutputFormat.values()) {
                if (format.name.equalsIgnoreCase(formatName)) {
                    return format.formatter;
                }
            }
            return SIMPLE.formatter; // default
        }
    }

    /**
     * Main entry point for CLI.
     * Usage: java LongestCommonSubsequenceCli [--format=FORMAT] [string1] [string2]
     * Supported formats: simple (default), detailed, json, csv, compact
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        String s1;
        String s2;
        OutputFormat format = OutputFormat.SIMPLE;

        // Parse arguments
        if (args.length == 0) {
            s1 = TestData.EXAMPLE_S1;
            s2 = TestData.EXAMPLE_S2;
            System.out.println(DEFAULT_ARGS_MESSAGE);
            System.out.println(USAGE_MESSAGE + "\n");
        } else if (args.length == 1 && args[0].startsWith("--format=")) {
            // Format specified but no strings - use defaults
            String formatName = args[0].substring("--format=".length());
            format = OutputFormat.SIMPLE;
            for (OutputFormat f : OutputFormat.values()) {
                if (f.name.equalsIgnoreCase(formatName)) {
                    format = f;
                    break;
                }
            }
            s1 = TestData.EXAMPLE_S1;
            s2 = TestData.EXAMPLE_S2;
        } else if (args.length == 2) {
            // Two args: either both strings, or format + string (error)
            if (args[0].startsWith("--format=")) {
                System.err.println("Error: Expected both strings after format option");
                System.err.println(USAGE_MESSAGE);
                System.exit(1);
                return;
            }
            s1 = args[0];
            s2 = args[1];
        } else if (args.length == 3) {
            if (!args[0].startsWith("--format=")) {
                System.err.println(INVALID_ARGS_MESSAGE);
                System.err.println(USAGE_MESSAGE);
                System.exit(1);
                return;
            }
            String formatName = args[0].substring("--format=".length());
            format = OutputFormat.SIMPLE;
            for (OutputFormat f : OutputFormat.values()) {
                if (f.name.equalsIgnoreCase(formatName)) {
                    format = f;
                    break;
                }
            }
            s1 = args[1];
            s2 = args[2];
        } else {
            System.err.println(INVALID_ARGS_MESSAGE);
            System.err.println(USAGE_MESSAGE);
            System.exit(1);
            return;
        }

        try {
            LcsInput input = new LcsInput(s1, s2);
            LcsSolver solver = LcsSolverFactory.standard();
            LcsResult result = solver.solve(input);

            // Output results using selected formatter
            System.out.println(format.formatter.format(s1, s2, result));
        } catch (InvalidInputException e) {
            System.err.println("Input Error: " + e.getMessage());
            System.exit(1);
        } catch (LcsException e) {
            System.err.println("LCS Error: " + e.getMessage());
            System.exit(1);
        }
    }
}
