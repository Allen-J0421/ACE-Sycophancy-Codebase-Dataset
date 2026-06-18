package lru;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A least-recently-used cache with access-order eviction.
 */
public final class LRUCache<K, V> {
    private final int capacity;
    private final LinkedHashMap<K, V> entries;

    public LRUCache(int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException("capacity must be non-negative");
        }

        this.capacity = capacity;
        this.entries = new LinkedHashMap<K, V>(16, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > LRUCache.this.capacity;
            }
        };
    }

    public int capacity() {
        return capacity;
    }

    public V get(K key) {
        return entries.get(Objects.requireNonNull(key, "key"));
    }

    public V getOrDefault(K key, V defaultValue) {
        V value = get(key);
        return value != null ? value : defaultValue;
    }

    public void put(K key, V value) {
        entries.put(Objects.requireNonNull(key, "key"), Objects.requireNonNull(value, "value"));
    }

    public V remove(K key) {
        return entries.remove(Objects.requireNonNull(key, "key"));
    }

    public boolean containsKey(K key) {
        return entries.containsKey(Objects.requireNonNull(key, "key"));
    }

    public void clear() {
        entries.clear();
    }

    public int size() {
        return entries.size();
    }

    public boolean isEmpty() {
        return entries.isEmpty();
    }
}
