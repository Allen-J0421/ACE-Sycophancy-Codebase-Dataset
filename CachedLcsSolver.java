import java.util.HashMap;
import java.util.Map;

/**
 * Decorator pattern implementation for caching LCS results.
 * Wraps any LcsSolver implementation and caches results for repeated queries.
 *
 * Useful when the same string pairs are compared multiple times.
 * Uses robust CacheKey class to prevent collision issues and ensure correctness
 * across different argument orderings.
 */
class CachedLcsSolver implements LcsSolver {
    private final LcsSolver delegate;
    private final Map<CacheKey, Integer> cache;

    /**
     * Creates a cached solver wrapping the provided solver.
     *
     * @param delegate the underlying LcsSolver to cache results for
     */
    public CachedLcsSolver(LcsSolver delegate) {
        this.delegate = delegate;
        this.cache = new HashMap<>();
    }

    /**
     * Solves LCS with caching. Returns cached result if available.
     * Automatically handles symmetry: lcs(A,B) and lcs(B,A) share same cache entry.
     *
     * @param input the LcsInput containing two strings
     * @return LcsResult with the length of the LCS (from cache if available)
     */
    @Override
    public LcsResult solve(LcsInput input) {
        CacheKey key = new CacheKey(input.getFirstString(), input.getSecondString());

        // Check cache first (O(1) with CacheKey.equals and hashCode)
        if (cache.containsKey(key)) {
            return new LcsResult(cache.get(key));
        }

        // Compute and cache the result
        LcsResult result = delegate.solve(input);
        cache.put(key, result.getLength());

        return result;
    }

    /**
     * Clears the cache.
     * Useful between independent test runs or to free memory.
     */
    public void clearCache() {
        cache.clear();
    }

    /**
     * Returns the current cache size.
     *
     * @return number of entries in the cache
     */
    public int getCacheSize() {
        return cache.size();
    }
}
