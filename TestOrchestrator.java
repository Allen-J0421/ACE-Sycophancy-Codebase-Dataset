/**
 * Comprehensive test orchestrator running all test suites.
 * Aggregates results, generates reports, and validates ecosystem health.
 */
class TestOrchestrator {

    static class TestResult {
        final String testName;
        final boolean passed;
        final long durationNanos;
        final String message;

        TestResult(String testName, boolean passed, long durationNanos, String message) {
            this.testName = testName;
            this.passed = passed;
            this.durationNanos = durationNanos;
            this.message = message;
        }

        double durationMs() {
            return durationNanos / 1_000_000.0;
        }

        @Override
        public String toString() {
            String status = passed ? "✓ PASS" : "✗ FAIL";
            return String.format("%s: %s (%.1f ms)", status, testName, durationMs());
        }
    }

    static class TestReport {
        final String suiteName;
        final java.util.List<TestResult> results;

        TestReport(String suiteName) {
            this.suiteName = suiteName;
            this.results = new java.util.ArrayList<>();
        }

        void addResult(TestResult result) {
            results.add(result);
        }

        int passCount() {
            return (int) results.stream().filter(r -> r.passed).count();
        }

        int failCount() {
            return (int) results.stream().filter(r -> !r.passed).count();
        }

        double totalDurationMs() {
            return results.stream().mapToLong(r -> r.durationNanos).sum() / 1_000_000.0;
        }

        double averageDurationMs() {
            return results.isEmpty() ? 0 : totalDurationMs() / results.size();
        }

        void print() {
            System.out.println("\n" + "=".repeat(70));
            System.out.println(suiteName);
            System.out.println("=".repeat(70));

            for (TestResult result : results) {
                System.out.println(result);
            }

            System.out.println();
            System.out.println(String.format(
                    "Summary: %d passed, %d failed (%.1f ms total)",
                    passCount(), failCount(), totalDurationMs()
            ));
        }
    }

    static class AggregateReport {
        final java.util.List<TestReport> suites;

        AggregateReport() {
            this.suites = new java.util.ArrayList<>();
        }

        void addSuite(TestReport suite) {
            suites.add(suite);
        }

        int totalTests() {
            return (int) suites.stream().flatMap(s -> s.results.stream()).count();
        }

        int totalPass() {
            return (int) suites.stream().mapToInt(TestReport::passCount).sum();
        }

        int totalFail() {
            return (int) suites.stream().mapToInt(TestReport::failCount).sum();
        }

        double totalDurationMs() {
            return suites.stream().mapToDouble(TestReport::totalDurationMs).sum();
        }

        void printSummary() {
            System.out.println("\n" + "=".repeat(70));
            System.out.println("COMPLETE TEST SUMMARY");
            System.out.println("=".repeat(70));
            System.out.println();

            System.out.println("Test Suites: " + suites.size());
            System.out.println("Total Tests: " + totalTests());
            System.out.println(String.format("Passed: %d (%.1f%%)", totalPass(),
                    100.0 * totalPass() / Math.max(1, totalTests())));
            System.out.println(String.format("Failed: %d", totalFail()));
            System.out.println(String.format("Total Duration: %.1f ms", totalDurationMs()));
            System.out.println();

            if (totalFail() == 0) {
                System.out.println("✓ ALL TESTS PASSED");
            } else {
                System.out.println("✗ SOME TESTS FAILED");
            }

            System.out.println("=".repeat(70));
        }
    }

    public static void main(String[] args) {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("LCS ECOSYSTEM - COMPREHENSIVE TEST ORCHESTRATION");
        System.out.println("=".repeat(70));

        AggregateReport aggregate = new AggregateReport();

        // Run all test suites
        aggregate.addSuite(runTestSuite("Fluent API & Diffing", () -> {
            TestFluentApiAndDiff.testFluentComparison();
            TestFluentApiAndDiff.testBatchQueries();
            TestFluentApiAndDiff.testComprehensiveAnalysis();
            TestFluentApiAndDiff.testDiffing();
        }));

        aggregate.addSuite(runTestSuite("Configuration & Validation", () -> {
            TestConfigAndValidation.testConfigBuilder();
            TestConfigAndValidation.testPresetConfigs();
            TestConfigAndValidation.testValidation();
        }));

        aggregate.addSuite(runTestSuite("Property-Based Tests", () -> {
            PropertyBasedTests.testSymmetryProperty();
            PropertyBasedTests.testMonotonicityProperty();
            PropertyBasedTests.testSubsequenceProperty();
            PropertyBasedTests.testIdempotencyProperty();
            PropertyBasedTests.testCompositionProperty();
            PropertyBasedTests.testScalingProperty();
        }));

        aggregate.addSuite(runTestSuite("Advanced Caching", () -> {
            TestAdvancedCaching.testLruCache();
            TestAdvancedCaching.testTtlCache();
            TestAdvancedCaching.testLruCachedSolver();
            TestAdvancedCaching.testTtlCachedSolver();
            TestAdvancedCaching.testPerformanceProfiler();
        }));

        aggregate.addSuite(runTestSuite("Adaptive Solver", () -> {
            TestAdaptiveSolver.testInputProfile();
            TestAdaptiveSolver.testAdaptiveSelection();
            TestAdaptiveSolver.testSelfOptimizing();
        }));

        aggregate.addSuite(runTestSuite("Specialized Solvers", () -> {
            TestSpecializedSolvers.testApproximateLcsSolver();
            TestSpecializedSolvers.testSubstringLcsSolver();
        }));

        aggregate.addSuite(runTestSuite("Integration", () -> {
            IntegrationSuite.testFluentApiWithConfig();
            IntegrationSuite.testValidationWithConfig();
            IntegrationSuite.testNormalizationWithDiffing();
            IntegrationSuite.testBatchProcessingWithAnalysis();
            IntegrationSuite.testSpecializedSolversWithMetrics();
        }));

        // Print individual suite results
        for (TestReport suite : aggregate.suites) {
            suite.print();
        }

        // Print aggregate summary
        aggregate.printSummary();
    }

    private static TestReport runTestSuite(String suiteName, TestRunner runner) {
        TestReport report = new TestReport(suiteName);

        try {
            long startNano = System.nanoTime();
            runner.run();
            long elapsed = System.nanoTime() - startNano;

            report.addResult(new TestResult(suiteName + " - complete", true, elapsed, ""));
        } catch (AssertionError e) {
            report.addResult(new TestResult(suiteName, false, 0, e.getMessage()));
        } catch (Exception e) {
            report.addResult(new TestResult(suiteName, false, 0, e.getClass().getSimpleName()));
        }

        return report;
    }

    @FunctionalInterface
    interface TestRunner {
        void run() throws Exception;
    }
}
