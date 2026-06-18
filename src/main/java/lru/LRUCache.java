package lru;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A least-recently-used cache with access-order eviction.
 */
public final class LRUCache<K, V> implements Cache<K, V> {
    private final int capacity;
    private final EvictingLinkedHashMap<K, V> entries;

    public LRUCache(int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException("capacity must be non-negative");
        }

        this.capacity = capacity;
        this.entries = new EvictingLinkedHashMap<>(capacity);
    }

    @Override
    public int capacity() {
        return capacity;
    }

    @Override
    public V get(K key) {
        return entries.get(Objects.requireNonNull(key, "key"));
    }

    @Override
    public void put(K key, V value) {
        entries.put(Objects.requireNonNull(key, "key"), Objects.requireNonNull(value, "value"));
    }

    @Override
    public V remove(K key) {
        return entries.remove(Objects.requireNonNull(key, "key"));
    }

    @Override
    public boolean containsKey(K key) {
        return entries.containsKey(Objects.requireNonNull(key, "key"));
    }

    @Override
    public void clear() {
        entries.clear();
    }

    @Override
    public int size() {
        return entries.size();
    }

    private static final class EvictingLinkedHashMap<K, V> extends LinkedHashMap<K, V> {
        private final int capacity;

        EvictingLinkedHashMap(int capacity) {
            super(16, 0.75f, true);
            this.capacity = capacity;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            return size() > capacity;
        }
    }
}
