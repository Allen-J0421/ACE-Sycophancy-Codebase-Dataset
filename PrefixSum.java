import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Computes prefix sum arrays efficiently.
 * The prefix sum at index i is the sum of all elements from index 0 to i.
 */
public class PrefixSum {

    private final List<Long> cache;
    private final boolean cacheEnabled;

    /**
     * Creates a PrefixSum instance with optional caching.
     *
     * @param cacheEnabled whether to cache computation results
     */
    public PrefixSum(boolean cacheEnabled) {
        this.cacheEnabled = cacheEnabled;
        this.cache = cacheEnabled ? new ArrayList<>() : null;
    }

    /**
     * Creates a PrefixSum instance with caching disabled.
     */
    public PrefixSum() {
        this(false);
    }

    /**
     * Computes the prefix sum of an array.
     *
     * @param arr the input array
     * @return a list where each element is the prefix sum up to that index
     * @throws IllegalArgumentException if the array is null or empty
     */
    public List<Long> compute(int[] arr) {
        Objects.requireNonNull(arr, "Input array cannot be null");
        if (arr.length == 0) {
            throw new IllegalArgumentException("Input array cannot be empty");
        }

        List<Long> result = new ArrayList<>(arr.length);
        long sum = 0;

        for (int value : arr) {
            sum += value;
            result.add(sum);
        }

        if (cacheEnabled) {
            cache.addAll(result);
        }

        return result;
    }

    /**
     * Gets cached results from the most recent computation.
     *
     * @return the cached prefix sum, or an empty list if caching is disabled or no computation has been done
     */
    public List<Long> getCachedResult() {
        return cacheEnabled ? new ArrayList<>(cache) : new ArrayList<>();
    }

    /**
     * Static convenience method for single computation without caching.
     *
     * @param arr the input array
     * @return a list where each element is the prefix sum up to that index
     */
    public static List<Long> computePrefixSum(int[] arr) {
        return new PrefixSum().compute(arr);
    }
}
