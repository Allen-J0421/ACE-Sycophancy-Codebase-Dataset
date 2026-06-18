import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Computes prefix sum arrays efficiently with caching and statistics.
 * The prefix sum at index i is the sum of all elements from index 0 to i.
 */
public class PrefixSum {

    private final List<Long> cache;
    private final boolean cacheEnabled;
    private final boolean clearCacheOnCompute;
    private int computationCount;

    /**
     * Creates a PrefixSum instance with configuration.
     *
     * @param cacheEnabled whether to cache computation results
     * @param clearCacheOnCompute whether to clear cache before each computation
     */
    private PrefixSum(boolean cacheEnabled, boolean clearCacheOnCompute) {
        this.cacheEnabled = cacheEnabled;
        this.clearCacheOnCompute = clearCacheOnCompute;
        this.cache = cacheEnabled ? new ArrayList<>() : null;
        this.computationCount = 0;
    }

    /**
     * Returns a builder for creating configured PrefixSum instances.
     *
     * @return a new Builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder class for fluent PrefixSum configuration.
     */
    public static class Builder {
        private boolean cacheEnabled = false;
        private boolean clearCacheOnCompute = false;

        public Builder withCache() {
            this.cacheEnabled = true;
            return this;
        }

        public Builder withCacheClear() {
            this.clearCacheOnCompute = true;
            return this;
        }

        public PrefixSum build() {
            return new PrefixSum(cacheEnabled, clearCacheOnCompute);
        }
    }

    /**
     * Computes the prefix sum of an array.
     *
     * @param arr the input array
     * @return an immutable result containing the prefix sums
     * @throws IllegalArgumentException if the array is null or empty
     */
    public PrefixSumResult compute(int[] arr) {
        Objects.requireNonNull(arr, "Input array cannot be null");
        if (arr.length == 0) {
            throw new IllegalArgumentException("Input array cannot be empty");
        }

        if (cacheEnabled && clearCacheOnCompute) {
            cache.clear();
        }

        List<Long> result = new ArrayList<>(arr.length);
        long sum = 0;

        for (int value : arr) {
            sum += value;
            result.add(sum);
        }

        if (cacheEnabled) {
            cache.clear();
            cache.addAll(result);
        }

        computationCount++;
        return new PrefixSumResult(result, arr.length, sum);
    }

    /**
     * Gets cached results from the most recent computation.
     *
     * @return an unmodifiable cached result, or an empty list if caching is disabled
     */
    public List<Long> getCachedResult() {
        return cacheEnabled && cache != null ? Collections.unmodifiableList(new ArrayList<>(cache)) : Collections.emptyList();
    }

    /**
     * Gets the number of computations performed.
     *
     * @return the computation count
     */
    public int getComputationCount() {
        return computationCount;
    }

    /**
     * Static convenience method for single computation without caching.
     *
     * @param arr the input array
     * @return the result of the prefix sum computation
     */
    public static PrefixSumResult computePrefixSum(int[] arr) {
        return new PrefixSum(false, false).compute(arr);
    }

    /**
     * Immutable result wrapper for prefix sum computations.
     */
    public static class PrefixSumResult {
        private final List<Long> values;
        private final int inputSize;
        private final long totalSum;

        private PrefixSumResult(List<Long> values, int inputSize, long totalSum) {
            this.values = Collections.unmodifiableList(new ArrayList<>(values));
            this.inputSize = inputSize;
            this.totalSum = totalSum;
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

        @Override
        public String toString() {
            return "PrefixSumResult{" +
                    "values=" + values +
                    ", totalSum=" + totalSum +
                    ", average=" + String.format("%.2f", getAverage()) +
                    '}';
        }
    }
}
