/**
 * Comprehensive performance profiling for LCS solvers.
 * Tracks timing, memory, cache efficiency, and generates detailed reports.
 */
class PerformanceProfiler {

    /**
     * Execution metrics for a single LCS solve operation.
     */
    static class ExecutionMetrics {
        final long elapsedNanos;
        final long memoryUsedBytes;
        final boolean cacheHit;
        final String solverName;

        ExecutionMetrics(long elapsedNanos, long memoryUsedBytes, boolean cacheHit, String solverName) {
            this.elapsedNanos = elapsedNanos;
            this.memoryUsedBytes = memoryUsedBytes;
            this.cacheHit = cacheHit;
            this.solverName = solverName;
        }

        double elapsedMillis() {
            return elapsedNanos / 1_000_000.0;
        }

        double memoryUsedMb() {
            return memoryUsedBytes / (1024.0 * 1024.0);
        }

        @Override
        public String toString() {
            return String.format(
                    "%s: %.3f ms, %.2f MB, %s",
                    solverName, elapsedMillis(), memoryUsedMb(),
                    cacheHit ? "CACHE_HIT" : "COMPUTED"
            );
        }
    }

    /**
     * Aggregated metrics across multiple runs.
     */
    static class AggregateMetrics {
        final String solverName;
        final int runCount;
        final long totalNanos;
        final long maxNanos;
        final long minNanos;
        final int cacheHits;
        final long avgMemoryBytes;

        AggregateMetrics(String solverName, java.util.List<ExecutionMetrics> metrics) {
            this.solverName = solverName;
            this.runCount = metrics.size();
            this.totalNanos = metrics.stream().mapToLong(m -> m.elapsedNanos).sum();
            this.maxNanos = metrics.stream().mapToLong(m -> m.elapsedNanos).max().orElse(0);
            this.minNanos = metrics.stream().mapToLong(m -> m.elapsedNanos).min().orElse(0);
            this.cacheHits = (int) metrics.stream().filter(m -> m.cacheHit).count();
            this.avgMemoryBytes = Math.round(
                    metrics.stream().mapToLong(m -> m.memoryUsedBytes).average().orElse(0)
            );
        }

        double avgNanos() {
            return runCount > 0 ? (double) totalNanos / runCount : 0;
        }

        double avgMillis() {
            return avgNanos() / 1_000_000.0;
        }

        double cacheHitRate() {
            return runCount > 0 ? (100.0 * cacheHits) / runCount : 0;
        }

        @Override
        public String toString() {
            return String.format(
                    "%s: %.3f ms avg (%.3f min, %.3f max), %.1f%% cache hits, %.2f MB avg",
                    solverName, avgMillis(), minNanos / 1_000_000.0, maxNanos / 1_000_000.0,
                    cacheHitRate(), avgMemoryBytes / (1024.0 * 1024.0)
            );
        }
    }

    /**
     * Profile a single solve operation.
     */
    static ExecutionMetrics profile(LcsSolver solver, String s1, String s2) {
        Runtime runtime = Runtime.getRuntime();

        // Record memory before
        long memBefore = runtime.totalMemory() - runtime.freeMemory();

        // Time the solve
        long startNano = System.nanoTime();
        LcsResult result = solver.solve(new LcsInput(s1, s2));
        long elapsedNano = System.nanoTime() - startNano;

        // Record memory after
        long memAfter = runtime.totalMemory() - runtime.freeMemory();
        long memUsed = Math.max(0, memAfter - memBefore);

        // Detect cache hit (heuristic: very fast execution)
        boolean cacheHit = elapsedNano < 100_000; // < 0.1ms typically indicates cache

        return new ExecutionMetrics(elapsedNano, memUsed, cacheHit, solver.toString());
    }

    /**
     * Profile multiple runs of a solver.
     */
    static AggregateMetrics profileMultiple(LcsSolver solver, String s1, String s2, int runs) {
        java.util.List<ExecutionMetrics> metrics = new java.util.ArrayList<>();

        for (int i = 0; i < runs; i++) {
            metrics.add(profile(solver, s1, s2));
        }

        return new AggregateMetrics(solver.toString(), metrics);
    }

    /**
     * Compare multiple solvers on same input.
     */
    static void comparesolvers(String s1, String s2, int runs, LcsSolver... solvers) {
        System.out.println("=".repeat(70));
        System.out.println("Performance Comparison: " + s1.length() + " × " + s2.length() + " chars, " + runs + " runs");
        System.out.println("=".repeat(70));
        System.out.println();

        for (LcsSolver solver : solvers) {
            AggregateMetrics metrics = profileMultiple(solver, s1, s2, runs);
            System.out.println(metrics);
        }

        System.out.println();
    }

    /**
     * Profile scaling behavior across input sizes.
     */
    static void profileScaling(LcsSolver solver, int minSize, int maxSize, int step) {
        System.out.println("=".repeat(70));
        System.out.println("Scaling Profile: " + solver);
        System.out.println("=".repeat(70));
        System.out.println();

        for (int size = minSize; size <= maxSize; size += step) {
            String s1 = "A".repeat(size);
            String s2 = "B".repeat(size);

            AggregateMetrics metrics = profileMultiple(solver, s1, s2, 3);
            System.out.println(String.format("Size %5d: %.3f ms avg", size, metrics.avgMillis()));
        }

        System.out.println();
    }

    /**
     * Profile cache efficiency for a cached solver.
     */
    static void profileCacheEfficiency(LcsSolver solver, String[] testStrings, int replayCount) {
        System.out.println("=".repeat(70));
        System.out.println("Cache Efficiency Profile: " + solver);
        System.out.println("=".repeat(70));
        System.out.println();

        // Warm-up: fill cache
        System.out.println("Warming up cache...");
        for (String s : testStrings) {
            solver.solve(new LcsInput(s, s));
        }

        // Measure cache hits
        long totalCacheHitTime = 0;
        int cacheHits = 0;

        for (int i = 0; i < replayCount; i++) {
            for (String s : testStrings) {
                long startNano = System.nanoTime();
                solver.solve(new LcsInput(s, s));
                long elapsedNano = System.nanoTime() - startNano;

                if (elapsedNano < 100_000) { // Heuristic for cache hit
                    totalCacheHitTime += elapsedNano;
                    cacheHits++;
                }
            }
        }

        int totalAttempts = testStrings.length * replayCount;
        double hitRate = (100.0 * cacheHits) / totalAttempts;
        double avgCacheHitNanos = cacheHits > 0 ? (double) totalCacheHitTime / cacheHits : 0;

        System.out.println(String.format("Cache hits: %d/%d (%.1f%%)", cacheHits, totalAttempts, hitRate));
        System.out.println(String.format("Avg cache hit time: %.3f μs", avgCacheHitNanos / 1000.0));
        System.out.println();
    }
}
