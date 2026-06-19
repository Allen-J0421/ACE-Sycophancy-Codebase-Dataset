/**
 * LRU (Least Recently Used) cache for LCS results.
 * Evicts least recently accessed entries when capacity is exceeded.
 * More efficient than unbounded cache for long-running applications.
 */
class LruCache {

    static class CacheEntry {
        final String key;
        final int value;
        long accessTime;

        CacheEntry(String key, int value) {
            this.key = key;
            this.value = value;
            this.accessTime = System.nanoTime();
        }
    }

    private final int capacity;
    private final java.util.LinkedHashMap<String, CacheEntry> map;

    /**
     * Creates an LRU cache with specified capacity.
     *
     * @param capacity maximum number of entries
     */
    LruCache(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        this.capacity = capacity;
        this.map = new java.util.LinkedHashMap<String, CacheEntry>(capacity, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(java.util.Map.Entry eldest) {
                return size() > LruCache.this.capacity;
            }
        };
    }

    /**
     * Puts a value in the cache.
     */
    synchronized void put(String key, int value) {
        map.put(key, new CacheEntry(key, value));
    }

    /**
     * Gets a value from cache, updating access time if hit.
     *
     * @return value or -1 if not found
     */
    synchronized int get(String key) {
        CacheEntry entry = map.get(key);
        if (entry != null) {
            entry.accessTime = System.nanoTime();
            return entry.value;
        }
        return -1;
    }

    /**
     * Checks if key exists in cache.
     */
    synchronized boolean containsKey(String key) {
        return map.containsKey(key);
    }

    /**
     * Clears the cache.
     */
    synchronized void clear() {
        map.clear();
    }

    /**
     * Gets current cache size.
     */
    synchronized int size() {
        return map.size();
    }

    /**
     * Gets cache capacity.
     */
    int getCapacity() {
        return capacity;
    }

    /**
     * Gets cache statistics.
     */
    synchronized CacheStats getStats() {
        return new CacheStats(map.size(), capacity);
    }

    static class CacheStats {
        final int entriesStored;
        final int capacity;
        final double utilizationPercent;

        CacheStats(int entriesStored, int capacity) {
            this.entriesStored = entriesStored;
            this.capacity = capacity;
            this.utilizationPercent = (100.0 * entriesStored) / capacity;
        }

        @Override
        public String toString() {
            return String.format("Cache: %d/%d entries (%.1f%% full)",
                    entriesStored, capacity, utilizationPercent);
        }
    }

    @Override
    public String toString() {
        return String.format("LruCache(capacity=%d, entries=%d)", capacity, map.size());
    }
}

/**
 * LCS solver using LRU caching strategy.
 */
class LruCachedLcsSolver implements LcsSolver {

    final LcsSolver baseSolver;
    final LruCache cache;

    /**
     * Creates a cached solver with LRU eviction.
     *
     * @param baseSolver underlying solver
     * @param cacheCapacity max number of cached results
     */
    LruCachedLcsSolver(LcsSolver baseSolver, int cacheCapacity) {
        this.baseSolver = baseSolver;
        this.cache = new LruCache(cacheCapacity);
    }

    @Override
    public LcsResult solve(LcsInput input) {
        String key = normalizeKey(input.getFirstString(), input.getSecondString());

        int cachedResult = cache.get(key);
        if (cachedResult >= 0) {
            return new LcsResult(cachedResult);
        }

        LcsResult result = baseSolver.solve(input);
        cache.put(key, result.getLength());
        return result;
    }

    /**
     * Gets cache statistics.
     */
    LruCache.CacheStats getCacheStats() {
        return cache.getStats();
    }

    private String normalizeKey(String s1, String s2) {
        String[] sorted = {s1, s2};
        java.util.Arrays.sort(sorted);
        return sorted[0].length() + "|" + sorted[0] + "|" + sorted[1].length() + "|" + sorted[1];
    }

    @Override
    public String toString() {
        return String.format("LruCachedLcsSolver(capacity=%d)", cache.getCapacity());
    }
}

/**
 * Time-limited cache entry that expires after TTL.
 */
class TtlCacheEntry {
    final String key;
    final int value;
    final long createdNanos;
    final long ttlNanos;

    TtlCacheEntry(String key, int value, long ttlMillis) {
        this.key = key;
        this.value = value;
        this.createdNanos = System.nanoTime();
        this.ttlNanos = ttlMillis * 1_000_000L;
    }

    boolean isExpired() {
        return System.nanoTime() - createdNanos > ttlNanos;
    }

    long ageMillis() {
        return (System.nanoTime() - createdNanos) / 1_000_000L;
    }
}

/**
 * TTL-based cache for LCS results that expire after specified duration.
 */
class TtlCache {

    private final java.util.Map<String, TtlCacheEntry> map = new java.util.HashMap<>();
    private final long ttlMillis;

    /**
     * Creates a TTL cache.
     *
     * @param ttlMillis time-to-live in milliseconds
     */
    TtlCache(long ttlMillis) {
        if (ttlMillis <= 0) {
            throw new IllegalArgumentException("TTL must be positive");
        }
        this.ttlMillis = ttlMillis;
    }

    /**
     * Puts a value in the cache.
     */
    synchronized void put(String key, int value) {
        map.put(key, new TtlCacheEntry(key, value, ttlMillis));
    }

    /**
     * Gets a value from cache if not expired.
     *
     * @return value or -1 if not found or expired
     */
    synchronized int get(String key) {
        TtlCacheEntry entry = map.get(key);
        if (entry != null && !entry.isExpired()) {
            return entry.value;
        }
        if (entry != null) {
            map.remove(key); // Clean up expired
        }
        return -1;
    }

    /**
     * Clears the cache and removes expired entries.
     */
    synchronized void cleanup() {
        map.entrySet().removeIf(e -> e.getValue().isExpired());
    }

    /**
     * Clears all entries.
     */
    synchronized void clear() {
        map.clear();
    }

    synchronized int size() {
        return map.size();
    }

    @Override
    public String toString() {
        return String.format("TtlCache(ttl=%dms, entries=%d)", ttlMillis, map.size());
    }
}

/**
 * LCS solver using TTL-based caching.
 */
class TtlCachedLcsSolver implements LcsSolver {

    final LcsSolver baseSolver;
    final TtlCache cache;
    final long ttlMillis;

    /**
     * Creates a TTL-cached solver.
     *
     * @param baseSolver underlying solver
     * @param ttlMillis cache entry lifetime in milliseconds
     */
    TtlCachedLcsSolver(LcsSolver baseSolver, long ttlMillis) {
        this.baseSolver = baseSolver;
        this.ttlMillis = ttlMillis;
        this.cache = new TtlCache(ttlMillis);
    }

    @Override
    public LcsResult solve(LcsInput input) {
        String key = normalizeKey(input.getFirstString(), input.getSecondString());

        int cachedResult = cache.get(key);
        if (cachedResult >= 0) {
            return new LcsResult(cachedResult);
        }

        LcsResult result = baseSolver.solve(input);
        cache.put(key, result.getLength());
        return result;
    }

    /**
     * Cleans up expired entries.
     */
    void cleanup() {
        cache.cleanup();
    }

    private String normalizeKey(String s1, String s2) {
        String[] sorted = {s1, s2};
        java.util.Arrays.sort(sorted);
        return sorted[0].length() + "|" + sorted[0] + "|" + sorted[1].length() + "|" + sorted[1];
    }

    @Override
    public String toString() {
        return String.format("TtlCachedLcsSolver(ttl=%dms)", ttlMillis);
    }
}
