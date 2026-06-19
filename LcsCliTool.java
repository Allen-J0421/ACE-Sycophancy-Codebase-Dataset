/**
 * Command-line tool for LCS operations.
 * Provides interactive and batch modes with extensive configuration options.
 */
class LcsCliTool {

    static class Options {
        String command = "compare";
        String string1 = "";
        String string2 = "";
        String preset = "standard";
        boolean showAnalysis = false;
        boolean showDiff = false;
        String diffFormat = "visual";
        boolean profile = false;
        boolean adaptive = false;
        int runs = 1;

        boolean parse(String[] args) {
            for (int i = 0; i < args.length; i++) {
                String arg = args[i];
                switch (arg) {
                    case "-c":
                    case "--command":
                        if (i + 1 < args.length) command = args[++i];
                        break;
                    case "-s1":
                    case "--string1":
                        if (i + 1 < args.length) string1 = args[++i];
                        break;
                    case "-s2":
                    case "--string2":
                        if (i + 1 < args.length) string2 = args[++i];
                        break;
                    case "-p":
                    case "--preset":
                        if (i + 1 < args.length) preset = args[++i];
                        break;
                    case "-a":
                    case "--analysis":
                        showAnalysis = true;
                        break;
                    case "-d":
                    case "--diff":
                        showDiff = true;
                        break;
                    case "--diff-format":
                        if (i + 1 < args.length) diffFormat = args[++i];
                        break;
                    case "--profile":
                        profile = true;
                        break;
                    case "--adaptive":
                        adaptive = true;
                        break;
                    case "-r":
                    case "--runs":
                        if (i + 1 < args.length) runs = Integer.parseInt(args[++i]);
                        break;
                    case "-h":
                    case "--help":
                        printHelp();
                        return false;
                    default:
                        if (!arg.startsWith("-")) {
                            if (string1.isEmpty()) string1 = arg;
                            else if (string2.isEmpty()) string2 = arg;
                        }
                        break;
                }
            }
            return true;
        }

        void printHelp() {
            System.out.println("LCS CLI Tool - Usage Guide");
            System.out.println("=".repeat(70));
            System.out.println();
            System.out.println("Commands:");
            System.out.println("  compare       Compare two strings (default)");
            System.out.println("  batch         Compare multiple pairs");
            System.out.println("  analyze       Show comprehensive analysis");
            System.out.println("  benchmark     Run performance benchmark");
            System.out.println();
            System.out.println("Options:");
            System.out.println("  -s1, --string1 <s>     First string");
            System.out.println("  -s2, --string2 <s>     Second string");
            System.out.println("  -p, --preset <name>    Config preset");
            System.out.println("                          (standard, performance, strict,");
            System.out.println("                           lenient, text-matching, large-inputs)");
            System.out.println("  -a, --analysis         Show full analysis");
            System.out.println("  -d, --diff             Show diff");
            System.out.println("  --diff-format <fmt>    Diff format (visual, ascii, side-by-side, markdown)");
            System.out.println("  --profile              Profile performance");
            System.out.println("  --adaptive             Use adaptive solver");
            System.out.println("  -r, --runs <n>         Number of runs for profiling");
            System.out.println();
            System.out.println("Examples:");
            System.out.println("  java LcsCliTool -s1 AGGTAB -s2 GXTXAYB");
            System.out.println("  java LcsCliTool -s1 HELLO -s2 hello -p lenient");
            System.out.println("  java LcsCliTool -s1 ABC -s2 AXC -a -d");
            System.out.println("  java LcsCliTool -s1 TEST -s2 BEST --profile --runs 10");
            System.out.println();
        }
    }

    public static void main(String[] args) {
        Options opts = new Options();

        if (args.length == 0) {
            opts.printHelp();
            return;
        }

        if (!opts.parse(args)) {
            return;
        }

        try {
            switch (opts.command.toLowerCase()) {
                case "compare":
                    compareCommand(opts);
                    break;
                case "analyze":
                    analyzeCommand(opts);
                    break;
                case "batch":
                    batchCommand(opts);
                    break;
                case "benchmark":
                    benchmarkCommand(opts);
                    break;
                default:
                    System.err.println("Unknown command: " + opts.command);
                    opts.printHelp();
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    static void compareCommand(Options opts) {
        if (opts.string1.isEmpty() || opts.string2.isEmpty()) {
            System.err.println("Error: Both strings required");
            return;
        }

        System.out.println("=".repeat(70));
        System.out.println("LCS Comparison");
        System.out.println("=".repeat(70));
        System.out.println();

        // Validate input
        LcsValidator validator = getValidator(opts);
        ValidationResult validation = validator.validate(opts.string1, opts.string2);
        if (!validation.valid) {
            System.err.println("Validation failed: " + validation);
            return;
        }
        System.out.println("✓ " + validation);
        System.out.println();

        // Get solver
        LcsSolver solver = getSolver(opts);
        System.out.println("Solver: " + solver);
        System.out.println();

        // Compute
        long startNano = System.nanoTime();
        LcsResult result = solver.solve(new LcsInput(opts.string1, opts.string2));
        long elapsedNano = System.nanoTime() - startNano;

        System.out.println("Input 1: " + opts.string1 + " (" + opts.string1.length() + " chars)");
        System.out.println("Input 2: " + opts.string2 + " (" + opts.string2.length() + " chars)");
        System.out.println();
        System.out.println("LCS Length: " + result.getLength());
        System.out.println(String.format("Time: %.3f ms", elapsedNano / 1_000_000.0));
        System.out.println();

        // Analysis
        if (opts.showAnalysis) {
            var analysis = LcsQueries.analyze(opts.string1, opts.string2);
            System.out.println(analysis);
            System.out.println();
        }

        // Diff
        if (opts.showDiff) {
            String lcs = LcsSequenceReconstructor.reconstructLcs(opts.string1, opts.string2);
            String diff = getDiff(opts, opts.string1, opts.string2, lcs);
            System.out.println(diff);
        }

        // Profiling
        if (opts.profile) {
            System.out.println("Performance (3 runs):");
            var metrics = PerformanceProfiler.profileMultiple(solver, opts.string1, opts.string2, 3);
            System.out.println("  " + metrics);
        }
    }

    static void analyzeCommand(Options opts) {
        if (opts.string1.isEmpty() || opts.string2.isEmpty()) {
            System.err.println("Error: Both strings required");
            return;
        }

        System.out.println("=".repeat(70));
        System.out.println("Comprehensive Analysis");
        System.out.println("=".repeat(70));
        System.out.println();

        var analysis = LcsQueries.analyze(opts.string1, opts.string2);
        System.out.println(analysis);
        System.out.println();

        // Input profile if adaptive
        if (opts.adaptive) {
            System.out.println("Input Profile (for adaptive selection):");
            AdaptiveSolver.InputProfile profile = new AdaptiveSolver.InputProfile(
                    opts.string1, opts.string2
            );
            System.out.println("  " + profile);
            AdaptiveSolver adaptive = new AdaptiveSolver();
            System.out.println("  Recommended solver: " + adaptive.selectSolver(profile));
        }
    }

    static void batchCommand(Options opts) {
        System.out.println("=".repeat(70));
        System.out.println("Batch Comparison (Interactive)");
        System.out.println("=".repeat(70));
        System.out.println();

        LcsSolver solver = getSolver(opts);
        System.out.println("Solver: " + solver);
        System.out.println();
        System.out.println("Enter pairs (format: string1 TAB string2)");
        System.out.println("Enter 'done' to finish");
        System.out.println();

        java.util.List<Integer> results = new java.util.ArrayList<>();
        java.util.Scanner scanner = new java.util.Scanner(System.in);

        int pairNum = 1;
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.equalsIgnoreCase("done")) break;

            String[] parts = line.split("\t");
            if (parts.length != 2) {
                System.out.println("Invalid format. Use: string1 TAB string2");
                continue;
            }

            int length = solver.solve(new LcsInput(parts[0], parts[1])).getLength();
            results.add(length);
            System.out.println(String.format("Pair %d: LCS length = %d", pairNum++, length));
        }

        scanner.close();

        if (!results.isEmpty()) {
            System.out.println();
            System.out.println("Summary:");
            System.out.println("  Total pairs: " + results.size());
            System.out.println("  Average LCS length: " +
                    String.format("%.1f", results.stream().mapToInt(Integer::intValue).average().orElse(0)));
        }
    }

    static void benchmarkCommand(Options opts) {
        if (opts.string1.isEmpty() || opts.string2.isEmpty()) {
            System.err.println("Error: Both strings required");
            return;
        }

        System.out.println("=".repeat(70));
        System.out.println("Performance Benchmark");
        System.out.println("=".repeat(70));
        System.out.println();

        LcsSolver[] solvers = {
                new StandardLcsSolver(),
                new SpaceOptimizedLcsSolver(),
                LcsSolverFactory.cached(),
                new ApproximateLcsSolver()
        };

        System.out.println("Input: " + opts.string1.length() + " × " + opts.string2.length() + " chars");
        System.out.println("Runs: " + opts.runs);
        System.out.println();

        for (LcsSolver solver : solvers) {
            var metrics = PerformanceProfiler.profileMultiple(solver, opts.string1, opts.string2, opts.runs);
            System.out.println(metrics);
        }
    }

    static LcsSolver getSolver(Options opts) {
        if (opts.adaptive) {
            return new AdaptiveSolver();
        }

        return switch (opts.preset.toLowerCase()) {
            case "performance" -> new SpaceOptimizedLcsSolver();
            case "strict" -> new StandardLcsSolver();
            case "lenient" -> new NormalizingLcsSolver(
                    LcsSolverFactory.cached(),
                    InputNormalizers.caseInsensitive()
            );
            case "text-matching" -> new NormalizingLcsSolver(
                    LcsSolverFactory.cached(),
                    InputNormalizers.textOnly()
            );
            case "large-inputs" -> new ApproximateLcsSolver();
            default -> LcsSolverFactory.standard();
        };
    }

    static LcsValidator getValidator(Options opts) {
        return LcsValidators.standard();
    }

    static String getDiff(Options opts, String s1, String s2, String lcs) {
        return switch (opts.diffFormat.toLowerCase()) {
            case "ascii" -> LcsDiffer.asciiDiff(s1, s2, lcs);
            case "side-by-side" -> LcsDiffer.sideBySideDiff(s1, s2, lcs);
            case "markdown" -> LcsDiffer.markdownDiff(s1, s2, lcs);
            default -> LcsDiffer.visualDiff(s1, s2, lcs);
        };
    }
}
