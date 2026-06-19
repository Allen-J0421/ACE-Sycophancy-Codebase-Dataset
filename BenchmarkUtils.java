import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utilities for benchmarking and performance testing LCS solvers.
 * Enables consistent performance measurement and comparison.
 */
class BenchmarkUtils {

    /**
     * Measures the execution time of an action in nanoseconds.
     * Includes JVM warmup to account for JIT compilation.
     *
     * @param action the code to benchmark
     * @return execution time in nanoseconds
     */
    static long measureNanos(Runnable action) {
        // Warmup: run the action multiple times to let JIT compile
        for (int i = 0; i < 5; i++) {
            action.run();
        }

        // Actual measurement
        long startTime = System.nanoTime();
        action.run();
        long endTime = System.nanoTime();

        return endTime - startTime;
    }

    /**
     * Measures the execution time of an action in milliseconds.
     *
     * @param action the code to benchmark
     * @return execution time in milliseconds
     */
    static long measureMillis(Runnable action) {
        return measureNanos(action) / 1_000_000;
    }

    /**
     * Compares relative performance of two implementations.
     * Returns ratio: baselineTime / subjectTime
     * Ratio > 1 means subject is faster
     * Ratio < 1 means subject is slower
     *
     * @param baseline reference implementation
     * @param subject  implementation to compare
     * @return performance ratio (baseline / subject)
     */
    static double comparePerformance(Runnable baseline, Runnable subject) {
        long baselineTime = measureNanos(baseline);
        long subjectTime = measureNanos(subject);

        if (subjectTime == 0) return Double.POSITIVE_INFINITY;
        return (double) baselineTime / subjectTime;
    }

    /**
     * Asserts that an action completes within a maximum time.
     * Useful for performance regression testing.
     *
     * @param action    the code to benchmark
     * @param maxNanos  maximum allowed execution time in nanoseconds
     * @throws AssertionError if action exceeds max time
     */
    static void assertPerformance(Runnable action, long maxNanos) {
        long actualTime = measureNanos(action);
        assert actualTime <= maxNanos :
                "Performance exceeded: " + actualTime + " ns > " + maxNanos + " ns";
    }

    /**
     * Benchmarks a solver across multiple test cases.
     * Returns comprehensive performance report.
     *
     * @param solver the LCS solver to benchmark
     * @param testCases array of {string1, string2} pairs
     * @return performance report with timing information
     */
    static PerformanceReport benchmark(LcsSolver solver, TestData.TestCase[] testCases) {
        List<Long> timings = new ArrayList<>();
        long totalTime = 0;

        for (TestData.TestCase testCase : testCases) {
            LcsInput input = new LcsInput(testCase.s1, testCase.s2);
            long time = measureNanos(() -> solver.solve(input));
            timings.add(time);
            totalTime += time;
        }

        return new PerformanceReport(solver.getClass().getSimpleName(), testCases.length,
                                      totalTime, timings);
    }

    /**
     * Compares performance of multiple solvers.
     * Returns comparative analysis with ratios and recommendations.
     *
     * @param solvers  map of solver name to solver instance
     * @param testCases array of {string1, string2} pairs
     * @return comparative performance report
     */
    static ComparativePerformanceReport compareSolvers(Map<String, LcsSolver> solvers,
                                                       TestData.TestCase[] testCases) {
        Map<String, PerformanceReport> reports = new HashMap<>();

        for (Map.Entry<String, LcsSolver> entry : solvers.entrySet()) {
            reports.put(entry.getKey(), benchmark(entry.getValue(), testCases));
        }

        return new ComparativePerformanceReport(reports);
    }

    /**
     * Performance report for a single solver.
     */
    static class PerformanceReport {
        final String solverName;
        final int testCaseCount;
        final long totalTimeNanos;
        final List<Long> individualTimings;

        PerformanceReport(String solverName, int testCaseCount, long totalTimeNanos,
                         List<Long> individualTimings) {
            this.solverName = solverName;
            this.testCaseCount = testCaseCount;
            this.totalTimeNanos = totalTimeNanos;
            this.individualTimings = individualTimings;
        }

        /**
         * Gets average time per test case in nanoseconds.
         */
        long getAverageNanos() {
            return testCaseCount > 0 ? totalTimeNanos / testCaseCount : 0;
        }

        /**
         * Gets average time per test case in milliseconds.
         */
        double getAverageMillis() {
            return getAverageNanos() / 1_000_000.0;
        }

        /**
         * Gets minimum time observed for any test case.
         */
        long getMinNanos() {
            return individualTimings.stream().mapToLong(Long::longValue).min().orElse(0);
        }

        /**
         * Gets maximum time observed for any test case.
         */
        long getMaxNanos() {
            return individualTimings.stream().mapToLong(Long::longValue).max().orElse(0);
        }

        @Override
        public String toString() {
            return String.format(
                    "%s: %.3f ms avg (min: %.3f ms, max: %.3f ms, total: %.3f ms for %d cases)",
                    solverName,
                    getAverageMillis(),
                    getMinNanos() / 1_000_000.0,
                    getMaxNanos() / 1_000_000.0,
                    totalTimeNanos / 1_000_000.0,
                    testCaseCount
            );
        }
    }

    /**
     * Comparative performance report for multiple solvers.
     */
    static class ComparativePerformanceReport {
        final Map<String, PerformanceReport> reports;

        ComparativePerformanceReport(Map<String, PerformanceReport> reports) {
            this.reports = reports;
        }

        /**
         * Gets the fastest solver name.
         */
        String getFastestSolver() {
            return reports.entrySet().stream()
                    .min((a, b) -> Long.compare(
                            a.getValue().totalTimeNanos,
                            b.getValue().totalTimeNanos))
                    .map(Map.Entry::getKey)
                    .orElse("Unknown");
        }

        /**
         * Gets performance ratio of subject vs baseline.
         * Ratio > 1 means subject is faster.
         */
        double getSpeedup(String baseline, String subject) {
            PerformanceReport baselineReport = reports.get(baseline);
            PerformanceReport subjectReport = reports.get(subject);

            if (baselineReport == null || subjectReport == null) return 0;
            if (subjectReport.totalTimeNanos == 0) return Double.POSITIVE_INFINITY;

            return (double) baselineReport.totalTimeNanos / subjectReport.totalTimeNanos;
        }

        /**
         * Prints comparative report to standard output.
         */
        void printReport() {
            System.out.println("=== Performance Report ===\n");

            for (PerformanceReport report : reports.values()) {
                System.out.println(report);
            }

            String fastest = getFastestSolver();
            System.out.println("\nFastest: " + fastest);

            // Show speedups relative to first (baseline)
            String baseline = reports.keySet().iterator().next();
            System.out.println("\nSpeedup vs " + baseline + ":");
            for (String solver : reports.keySet()) {
                if (!solver.equals(baseline)) {
                    double speedup = getSpeedup(baseline, solver);
                    System.out.printf("  %s: %.2fx%n", solver, speedup);
                }
            }
        }
    }
}
