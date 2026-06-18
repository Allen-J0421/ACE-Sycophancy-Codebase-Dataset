package graph.cache;

public class CacheEntry<T> {
    private final T value;
    private final long timestamp;
    private final long ttlMs;

    public CacheEntry(T value, long ttlMs) {
        this.value = value;
        this.timestamp = System.currentTimeMillis();
        this.ttlMs = ttlMs;
    }

    public T getValue() {
        return value;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - timestamp > ttlMs;
    }

    public long getAge() {
        return System.currentTimeMillis() - timestamp;
    }
}
