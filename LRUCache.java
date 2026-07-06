import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class LRUCache<K, V> implements Cache<K, V> {
    private final Map<K, V> map;

    public LRUCache(int capacity) {
        this.map = new EvictingLinkedHashMap<>(capacity);
    }

    @Override
    public V get(K key) {
        Objects.requireNonNull(key, "Cache key must not be null");
        return map.get(key);
    }

    @Override
    public void put(K key, V value) {
        Objects.requireNonNull(key, "Cache key must not be null");
        Objects.requireNonNull(value, "Cache value must not be null");
        map.put(key, value);
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public void clear() {
        map.clear();
    }

    private static class EvictingLinkedHashMap<K, V> extends LinkedHashMap<K, V> {
        private final int capacity;

        EvictingLinkedHashMap(int capacity) {
            super(capacity, 0.75f, true);
            this.capacity = capacity;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            return size() > capacity;
        }
    }
}
