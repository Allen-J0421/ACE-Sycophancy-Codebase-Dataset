import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class LRUCache<K, V> implements Cache<K, V> {
    private static final float LOAD_FACTOR = 0.75f;

    private final int capacity;
    private final Map<K, V> cache;

    public LRUCache(int capacity) {
        this.capacity = validateCapacity(capacity);
        this.cache = new LinkedHashMap<>(mapCapacityFor(capacity), LOAD_FACTOR, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > LRUCache.this.capacity;
            }
        };
    }

    @Override
    public Optional<V> get(K key) {
        return Optional.ofNullable(cache.get(requireKey(key)));
    }

    @Override
    public boolean containsKey(K key) {
        return cache.containsKey(requireKey(key));
    }

    @Override
    public void put(K key, V value) {
        cache.put(requireKey(key), requireValue(value));
    }

    @Override
    public Optional<V> remove(K key) {
        return Optional.ofNullable(cache.remove(requireKey(key)));
    }

    @Override
    public int size() {
        return cache.size();
    }

    @Override
    public int capacity() {
        return capacity;
    }

    @Override
    public void clear() {
        cache.clear();
    }

    private static int validateCapacity(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be greater than 0");
        }

        return capacity;
    }

    private static int mapCapacityFor(int maxEntries) {
        return Math.max(1, (int) Math.ceil(maxEntries / LOAD_FACTOR));
    }

    private K requireKey(K key) {
        return Objects.requireNonNull(key, "key must not be null");
    }

    private V requireValue(V value) {
        return Objects.requireNonNull(value, "value must not be null");
    }
}
