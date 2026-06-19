/**
 * Organized benchmark suite for LCS solver performance.
 * Provides structured testing across multiple scenarios and solvers.
 */
class BenchmarkSuite {

    /**
     * Benchmark scenario definition.
     */
    static class Scenario {
        final String name;
        final String s1;
        final String s2;
        final String description;

        Scenario(String name, String s1, String s2, String description) {
            this.name = name;
            this.s1 = s1;
            this.s2 = s2;
            this.description = description;
        }

        @Override
        public String toString() {
            return String.format("%s (%s, %s)", name, s1.length(), s2.length());
        }
    }

    /**
     * Small input scenarios (< 100 chars).
     */
    static Scenario[] smallScenarios() {
        return new Scenario[]{
                new Scenario("identical", "AGGTAB", "AGGTAB", "Identical strings"),
                new Scenario("different", "AGGTAB", "GXTXAYB", "Typical comparison"),
                new Scenario("empty", "", "ABC", "Empty string"),
                new Scenario("single", "A", "B", "Single character"),
                new Scenario("no_match", "AAA", "BBB", "No common characters")
        };
    }

    /**
     * Medium input scenarios (100-1000 chars).
     */
    static Scenario[] mediumScenarios() {
        return new Scenario[]{
                new Scenario("medium_text",
                        "The quick brown fox jumps over the lazy dog",
                        "The quack brown fox jumps over the lazy dog",
                        "Natural text comparison"),
                new Scenario("medium_similar",
                        generateRepeating("ABC", 50),
                        generateRepeating("ABD", 50),
                        "Highly similar patterns"),
                new Scenario("medium_random",
                        generateRandom(200, "ACGT"),
                        generateRandom(200, "ACGT"),
                        "Random DNA-like sequences")
        };
    }

    /**
     * Large input scenarios (1000-5000 chars).
     */
    static Scenario[] largeScenarios() {
        return new Scenario[]{
                new Scenario("large_similar",
                        generateRepeating("ABCDEFG", 200),
                        generateRepeating("ABCDEFG", 200),
                        "Large identical patterns"),
                new Scenario("large_random",
                        generateRandom(2000, "ACGT"),
                        generateRandom(2000, "ACGT"),
                        "Large random sequences"),
                new Scenario("large_different",
                        generateRandom(1000, "ABC"),
                        generateRandom(1000, "XYZ"),
                        "Large divergent sequences")
        };
    }

    /**
     * Solver set for comparison.
     */
    static LcsSolver[] solvers() {
        return new LcsSolver[]{
                new StandardLcsSolver(),
                new SpaceOptimizedLcsSolver(),
                LcsSolverFactory.cached(),
                new ApproximateLcsSolver()
        };
    }

    /**
     * Run all benchmarks and report results.
     */
    static void runAll() {
        System.out.println("=" .repeat(60));
        System.out.println("LCS Benchmark Suite");
        System.out.println("=".repeat(60));
        System.out.println();

        runScenarios("Small Inputs", smallScenarios());
        System.out.println();
        runScenarios("Medium Inputs", mediumScenarios());
        System.out.println();
        runScenarios("Large Inputs", largeScenarios());
    }

    private static void runScenarios(String title, Scenario[] scenarios) {
        System.out.println("--- " + title + " ---\n");

        for (Scenario scenario : scenarios) {
            System.out.println("Scenario: " + scenario.name + " (" + scenario.description + ")");
            System.out.println(String.format("  Sizes: %d × %d chars", scenario.s1.length(), scenario.s2.length()));

            LcsSolver[] allSolvers = solvers();
            long[][] times = new long[allSolvers.length][3]; // 3 runs each

            // Warmup
            for (LcsSolver solver : allSolvers) {
                solver.solve(new LcsInput(scenario.s1, scenario.s2));
            }

            // Benchmark
            for (int solverIdx = 0; solverIdx < allSolvers.length; solverIdx++) {
                for (int run = 0; run < 3; run++) {
                    long start = System.nanoTime();
                    allSolvers[solverIdx].solve(new LcsInput(scenario.s1, scenario.s2));
                    times[solverIdx][run] = System.nanoTime() - start;
                }

                long avgNanos = (times[solverIdx][0] + times[solverIdx][1] + times[solverIdx][2]) / 3;
                System.out.println(String.format("    %-25s: %.3f ms",
                        allSolvers[solverIdx].toString(), avgNanos / 1_000_000.0));
            }
            System.out.println();
        }
    }

    /**
     * Helper: Generate repeated pattern.
     */
    static String generateRepeating(String pattern, int reps) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < reps; i++) {
            sb.append(pattern);
        }
        return sb.toString();
    }

    /**
     * Helper: Generate random string from alphabet.
     */
    static String generateRandom(int length, String alphabet) {
        StringBuilder sb = new StringBuilder();
        java.util.Random rand = new java.util.Random(42); // Fixed seed for reproducibility
        for (int i = 0; i < length; i++) {
            sb.append(alphabet.charAt(rand.nextInt(alphabet.length())));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        runAll();
        System.out.println("Benchmark complete");
    }
}
