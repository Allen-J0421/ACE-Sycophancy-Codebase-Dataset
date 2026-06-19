import java.util.HashMap;
import java.util.Map;

/**
 * Decorator pattern implementation for caching LCS results.
 * Wraps any LcsSolver implementation and caches results for repeated queries.
 *
 * Useful when the same string pairs are compared multiple times.
 * Cache key is based on both strings to ensure correctness across different orderings.
 */
class CachedLcsSolver implements LcsSolver {
    private final LcsSolver delegate;
    private final Map<String, Integer> cache;

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
     *
     * @param input the LcsInput containing two strings
     * @return LcsResult with the length of the LCS (from cache if available)
     */
    @Override
    public LcsResult solve(LcsInput input) {
        String cacheKey = generateCacheKey(input.getFirstString(), input.getSecondString());

        // Check cache first
        if (cache.containsKey(cacheKey)) {
            return new LcsResult(cache.get(cacheKey));
        }

        // Compute and cache the result
        LcsResult result = delegate.solve(input);
        cache.put(cacheKey, result.getLength());

        return result;
    }

    /**
     * Generates a cache key from two strings.
     * Uses a canonical form to ensure symmetry (lcs(A,B) uses same cache as lcs(B,A)).
     *
     * @param s1 first string
     * @param s2 second string
     * @return cache key
     */
    private String generateCacheKey(String s1, String s2) {
        // Create symmetric cache key to handle both (s1,s2) and (s2,s1)
        if (s1.compareTo(s2) <= 0) {
            return s1 + "||" + s2;
        } else {
            return s2 + "||" + s1;
        }
    }

    /**
     * Clears the cache.
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
