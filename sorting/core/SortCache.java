package sorting.core;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SortCache<T extends Comparable<T>> {
    private final Map<String, CacheEntry<T>> cache;
    private final int maxSize;
    private volatile long hits = 0;
    private volatile long misses = 0;

    public SortCache(int maxSize) {
        this.cache = new ConcurrentHashMap<>();
        this.maxSize = maxSize;
    }

    public boolean get(String key, T[] array) {
        if (cache.size() > maxSize) {
            evictOldest();
        }

        CacheEntry<T> entry = cache.get(key);
        if (entry != null && entry.matches(array)) {
            hits++;
            System.arraycopy(entry.result, 0, array, 0, array.length);
            return true;
        }
        misses++;
        return false;
    }

    public void put(String key, T[] array) {
        if (cache.size() >= maxSize) {
            evictOldest();
        }

        T[] copy = array.clone();
        cache.put(key, new CacheEntry<>(copy, System.currentTimeMillis()));
    }

    private void evictOldest() {
        cache.entrySet().stream()
                .min((a, b) -> Long.compare(a.getValue().timestamp, b.getValue().timestamp))
                .ifPresent(entry -> cache.remove(entry.getKey()));
    }

    public double hitRate() {
        long total = hits + misses;
        return total == 0 ? 0.0 : (double) hits / total;
    }

    public void clear() {
        cache.clear();
        hits = 0;
        misses = 0;
    }

    public String stats() {
        return String.format("Cache stats: size=%d, hits=%d, misses=%d, hitRate=%.1f%%",
                cache.size(), hits, misses, hitRate() * 100);
    }

    private record CacheEntry<T>(T[] result, long timestamp) {
        boolean matches(T[] array) {
            return result.length == array.length;
        }
    }
}
