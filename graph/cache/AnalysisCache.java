package graph.cache;

import java.util.HashMap;
import java.util.Map;

public class AnalysisCache {
    private final Map<String, CacheEntry<?>> cache;
    private final long defaultTtlMs;

    public AnalysisCache(long defaultTtlMs) {
        this.cache = new HashMap<>();
        this.defaultTtlMs = defaultTtlMs;
    }

    public <T> void put(String key, T value) {
        cache.put(key, new CacheEntry<>(value, defaultTtlMs));
    }

    public <T> T get(String key, Class<T> type) {
        CacheEntry<?> entry = cache.get(key);
        if (entry == null || entry.isExpired()) {
            return null;
        }
        return type.cast(entry.getValue());
    }

    public boolean contains(String key) {
        CacheEntry<?> entry = cache.get(key);
        return entry != null && !entry.isExpired();
    }

    public void invalidate(String key) {
        cache.remove(key);
    }

    public void invalidateAll() {
        cache.clear();
    }

    public int size() {
        return (int) cache.values().stream()
                .filter(e -> !e.isExpired())
                .count();
    }
}
