import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Computes prefix sum arrays efficiently with caching, metrics, statistics, and pluggable strategies.
 * The prefix sum at index i is the sum of all elements from index 0 to i.
 */
public class PrefixSum {

    private final List<Long> cache;
    private final PrefixSumConfig config;
    private final ComputationStrategy strategy;
    private final List<ComputationListener> listeners;
    private int computationCount;
    private long totalComputationTime;
    private long peakMemoryUsed;

    /**
     * Creates a PrefixSum instance with default configuration and iterative strategy.
     */
    public PrefixSum() {
        this(PrefixSumConfig.defaults(), new IterativeStrategy());
    }

    /**
     * Creates a PrefixSum instance with custom configuration.
     *
     * @param config the configuration object
     */
    public PrefixSum(PrefixSumConfig config) {
        this(config, new IterativeStrategy());
    }

    /**
     * Creates a PrefixSum instance with custom configuration and strategy.
     *
     * @param config the configuration object
     * @param strategy the computation strategy
     */
    public PrefixSum(PrefixSumConfig config, ComputationStrategy strategy) {
        Objects.requireNonNull(config, "Configuration cannot be null");
        Objects.requireNonNull(strategy, "Strategy cannot be null");
        this.config = config;
        this.strategy = strategy;
        this.cache = config.isCacheEnabled() ? new ArrayList<>() : null;
        this.listeners = new ArrayList<>();
        this.computationCount = 0;
        this.totalComputationTime = 0;
        this.peakMemoryUsed = 0;
    }

    /**
     * Registers a listener for computation events.
     *
     * @param listener the listener to register
     */
    public void addListener(ComputationListener listener) {
        listeners.add(Objects.requireNonNull(listener, "Listener cannot be null"));
    }

    /**
     * Removes a listener.
     *
     * @param listener the listener to remove
     */
    public void removeListener(ComputationListener listener) {
        listeners.remove(listener);
    }

    /**
     * Computes the prefix sum of an array.
     *
     * @param arr the input array
     * @return an immutable result containing the prefix sums
     * @throws IllegalArgumentException if the array is null or empty
     */
    public PrefixSumResult compute(int[] arr) {
        InputValidator.validate(arr);

        try {
            notifyListenersStart(arr.length);

            long startTime = config.isTrackMetricsEnabled() ? System.nanoTime() : 0;
            Runtime runtime = config.isTrackMetricsEnabled() ? Runtime.getRuntime() : null;
            long memBefore = config.isTrackMetricsEnabled() ? runtime.totalMemory() - runtime.freeMemory() : 0;

            if (config.isCacheEnabled() && config.shouldClearCacheOnCompute()) {
                cache.clear();
            }

            List<Long> result = strategy.compute(arr);
            long sum = result.get(result.size() - 1);

            if (config.isCacheEnabled()) {
                cache.clear();
                cache.addAll(result);
            }

            long memAfter = config.isTrackMetricsEnabled() ? runtime.totalMemory() - runtime.freeMemory() : 0;
            long computationTime = config.isTrackMetricsEnabled() ? (System.nanoTime() - startTime) / 1_000_000 : 0;
            long memUsed = config.isTrackMetricsEnabled() ? Math.abs(memAfter - memBefore) : 0;

            computationCount++;
            totalComputationTime += computationTime;
            peakMemoryUsed = Math.max(peakMemoryUsed, memUsed);

            PrefixSumResult prefixSumResult = new PrefixSumResult(result, arr.length, sum, computationTime, memUsed);
            notifyListenersComplete(prefixSumResult);

            return prefixSumResult;

        } catch (Exception e) {
            notifyListenersError(e);
            throw e;
        }
    }

    private void notifyListenersStart(int arraySize) {
        for (ComputationListener listener : listeners) {
            listener.onComputationStart(arraySize);
        }
    }

    private void notifyListenersComplete(PrefixSumResult result) {
        for (ComputationListener listener : listeners) {
            listener.onComputationComplete(result);
        }
    }

    private void notifyListenersError(Exception exception) {
        for (ComputationListener listener : listeners) {
            listener.onComputationError(exception);
        }
    }

    /**
     * Gets cached results from the most recent computation.
     *
     * @return an unmodifiable cached result, or an empty list if caching is disabled
     */
    public List<Long> getCachedResult() {
        return config.isCacheEnabled() && cache != null ?
            Collections.unmodifiableList(new ArrayList<>(cache)) :
            Collections.emptyList();
    }

    /**
     * Gets computation metrics.
     *
     * @return metrics for this calculator's computations
     */
    public ComputationMetrics getMetrics() {
        return new ComputationMetrics(totalComputationTime, computationCount, peakMemoryUsed);
    }

    /**
     * Gets the computation strategy name.
     *
     * @return strategy name
     */
    public String getStrategyName() {
        return strategy.getName();
    }

    /**
     * Static convenience method for single computation without caching.
     *
     * @param arr the input array
     * @return the result of the prefix sum computation
     */
    public static PrefixSumResult computePrefixSum(int[] arr) {
        return new PrefixSum(PrefixSumConfig.defaults()).compute(arr);
    }

    /**
     * Immutable result wrapper for prefix sum computations.
     */
    public static class PrefixSumResult {
        private final List<Long> values;
        private final int inputSize;
        private final long totalSum;
        private final long computationTimeMs;
        private final long memoryUsedBytes;

        private PrefixSumResult(List<Long> values, int inputSize, long totalSum,
                               long computationTimeMs, long memoryUsedBytes) {
            this.values = Collections.unmodifiableList(new ArrayList<>(values));
            this.inputSize = inputSize;
            this.totalSum = totalSum;
            this.computationTimeMs = computationTimeMs;
            this.memoryUsedBytes = memoryUsedBytes;
        }

        public List<Long> getValues() {
            return values;
        }

        public int getInputSize() {
            return inputSize;
        }

        public long getTotalSum() {
            return totalSum;
        }

        public double getAverage() {
            return inputSize > 0 ? (double) totalSum / inputSize : 0.0;
        }

        public long getComputationTimeMs() {
            return computationTimeMs;
        }

        public long getMemoryUsedBytes() {
            return memoryUsedBytes;
        }

        @Override
        public String toString() {
            return "PrefixSumResult{" +
                    "values=" + values +
                    ", totalSum=" + totalSum +
                    ", average=" + String.format("%.2f", getAverage()) +
                    ", timeMs=" + computationTimeMs +
                    ", memoryBytes=" + memoryUsedBytes +
                    '}';
        }
    }
}
