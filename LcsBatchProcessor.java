/**
 * Interface for batch processing of multiple LCS computations.
 * Enables efficient processing of string pairs in bulk with optional caching
 * and parallelization.
 */
interface LcsBatchProcessor {
    /**
     * Processes multiple string pairs in a single batch operation.
     *
     * @param strings1 first set of strings
     * @param strings2 second set of strings (same length as strings1)
     * @return batch results with all LCS lengths
     * @throws IllegalArgumentException if arrays have different lengths
     */
    BatchResult processBatch(String[] strings1, String[] strings2);
}

/**
 * Result of batch LCS processing.
 * Contains LCS lengths for all pairs plus metadata.
 */
class BatchResult {
    final int[] lcsLengths;
    final long totalTimeMs;
    final int processedCount;

    /**
     * Constructs a batch result.
     *
     * @param lcsLengths array of LCS lengths (parallel to input pairs)
     * @param totalTimeMs total processing time in milliseconds
     * @param processedCount number of pairs processed
     */
    BatchResult(int[] lcsLengths, long totalTimeMs, int processedCount) {
        this.lcsLengths = lcsLengths;
        this.totalTimeMs = totalTimeMs;
        this.processedCount = processedCount;
    }

    /**
     * Gets the average LCS length across all pairs.
     */
    double getAverageLcsLength() {
        if (processedCount == 0) return 0;
        int sum = 0;
        for (int len : lcsLengths) {
            sum += len;
        }
        return (double) sum / processedCount;
    }

    /**
     * Gets average processing time per pair in milliseconds.
     */
    double getAverageTimePerPair() {
        if (processedCount == 0) return 0;
        return (double) totalTimeMs / processedCount;
    }

    @Override
    public String toString() {
        return String.format(
                "BatchResult: %d pairs, avg LCS=%.1f, avg time=%.2f ms/pair, total=%.0f ms",
                processedCount,
                getAverageLcsLength(),
                getAverageTimePerPair(),
                (double) totalTimeMs
        );
    }
}

/**
 * Sequential batch processor: processes pairs one-by-one.
 * Simple, deterministic, suitable for most use cases.
 */
class SequentialBatchProcessor implements LcsBatchProcessor {
    private final LcsSolver solver;

    /**
     * Creates a sequential processor with the specified solver.
     *
     * @param solver the LCS solver to use for each pair
     */
    SequentialBatchProcessor(LcsSolver solver) {
        this.solver = solver;
    }

    @Override
    public BatchResult processBatch(String[] strings1, String[] strings2) {
        if (strings1.length != strings2.length) {
            throw new IllegalArgumentException(
                    "Input arrays must have same length: " + strings1.length + " vs " + strings2.length);
        }

        int[] results = new int[strings1.length];
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < strings1.length; i++) {
            LcsInput input = new LcsInput(strings1[i], strings2[i]);
            results[i] = solver.solve(input).getLength();
        }

        long totalTime = System.currentTimeMillis() - startTime;
        return new BatchResult(results, totalTime, strings1.length);
    }
}

/**
 * Cached batch processor: applies caching across entire batch.
 * Ideal when many pairs might be duplicated or queried multiple times.
 */
class CachedBatchProcessor implements LcsBatchProcessor {
    private final LcsSolver cachedSolver;

    /**
     * Creates a cached processor with automatic caching.
     * Uses standard solver wrapped with caching.
     */
    CachedBatchProcessor() {
        this.cachedSolver = LcsSolverFactory.cached();
    }

    /**
     * Creates a cached processor with a specific base solver.
     *
     * @param baseSolver the solver to cache results for
     */
    CachedBatchProcessor(LcsSolver baseSolver) {
        this.cachedSolver = LcsSolverFactory.withCaching(baseSolver);
    }

    @Override
    public BatchResult processBatch(String[] strings1, String[] strings2) {
        if (strings1.length != strings2.length) {
            throw new IllegalArgumentException("Input arrays must have same length");
        }

        int[] results = new int[strings1.length];
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < strings1.length; i++) {
            LcsInput input = new LcsInput(strings1[i], strings2[i]);
            results[i] = cachedSolver.solve(input).getLength();
        }

        long totalTime = System.currentTimeMillis() - startTime;
        return new BatchResult(results, totalTime, strings1.length);
    }

    /**
     * Gets the underlying cached solver for cache inspection.
     *
     * @return the cached solver
     */
    LcsSolver getCachedSolver() {
        return cachedSolver;
    }
}

/**
 * Parallel batch processor: processes pairs concurrently using ForkJoinPool.
 * Best for large batches where parallelization overhead is justified.
 * Thread-safe implementation suitable for concurrent use.
 */
class ParallelBatchProcessor implements LcsBatchProcessor {
    private final LcsSolver solver;
    private final int parallelismThreshold;

    /**
     * Creates a parallel processor with specified solver.
     * Parallel threshold: switch to concurrent processing for batches > threshold.
     *
     * @param solver the LCS solver to use
     * @param parallelismThreshold minimum batch size for parallel processing
     */
    ParallelBatchProcessor(LcsSolver solver, int parallelismThreshold) {
        this.solver = solver;
        this.parallelismThreshold = parallelismThreshold;
    }

    /**
     * Creates a parallel processor with default threshold (10 pairs).
     *
     * @param solver the LCS solver to use
     */
    ParallelBatchProcessor(LcsSolver solver) {
        this(solver, 10);
    }

    @Override
    public BatchResult processBatch(String[] strings1, String[] strings2) {
        if (strings1.length != strings2.length) {
            throw new IllegalArgumentException("Input arrays must have same length");
        }

        // For small batches, use sequential processing
        if (strings1.length < parallelismThreshold) {
            return new SequentialBatchProcessor(solver).processBatch(strings1, strings2);
        }

        // For larger batches, use parallel processing
        int[] results = new int[strings1.length];
        long startTime = System.currentTimeMillis();

        // Use ForkJoinPool for parallelization
        java.util.concurrent.ForkJoinPool.commonPool().invoke(
                new LcsBatchTask(strings1, strings2, solver, results, 0, strings1.length)
        );

        long totalTime = System.currentTimeMillis() - startTime;
        return new BatchResult(results, totalTime, strings1.length);
    }

    /**
     * Internal ForkJoinTask for parallel batch processing.
     */
    private static class LcsBatchTask extends java.util.concurrent.RecursiveAction {
        private static final int THRESHOLD = 100; // Process up to 100 pairs sequentially

        final String[] strings1;
        final String[] strings2;
        final LcsSolver solver;
        final int[] results;
        final int start;
        final int end;

        LcsBatchTask(String[] strings1, String[] strings2, LcsSolver solver,
                     int[] results, int start, int end) {
            this.strings1 = strings1;
            this.strings2 = strings2;
            this.solver = solver;
            this.results = results;
            this.start = start;
            this.end = end;
        }

        @Override
        protected void compute() {
            if (end - start <= THRESHOLD) {
                // Process this chunk sequentially
                for (int i = start; i < end; i++) {
                    LcsInput input = new LcsInput(strings1[i], strings2[i]);
                    results[i] = solver.solve(input).getLength();
                }
            } else {
                // Split and process in parallel
                int mid = (start + end) / 2;
                LcsBatchTask left = new LcsBatchTask(strings1, strings2, solver, results, start, mid);
                LcsBatchTask right = new LcsBatchTask(strings1, strings2, solver, results, mid, end);
                invokeAll(left, right);
            }
        }
    }
}
