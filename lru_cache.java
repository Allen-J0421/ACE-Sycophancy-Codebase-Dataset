import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

interface Cache<K, V> {
    Optional<V> get(K key);
    void put(K key, V value);
    int size();
}

class LRUCache<K, V> implements Cache<K, V> {

    // accessOrder=true makes LinkedHashMap maintain LRU order on every get/put.
    // removeEldestEntry evicts the least-recently-used entry once capacity is exceeded.
    private static class EvictingLinkedHashMap<K, V> extends LinkedHashMap<K, V> {
        private final int capacity;

        EvictingLinkedHashMap(int capacity) {
            super(capacity + 1, 0.75f, true);
            this.capacity = capacity;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            return size() > capacity;
        }
    }

    private final Map<K, V> store;

    LRUCache(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive, got: " + capacity);
        }
        this.store = new EvictingLinkedHashMap<>(capacity);
    }

    @Override
    public Optional<V> get(K key) {
        return Optional.ofNullable(store.get(key));
    }

    @Override
    public void put(K key, V value) {
        store.put(key, value);
    }

    @Override
    public int size() {
        return store.size();
    }
}

class Main {
    public static void main(String[] args) {
        Cache<Integer, Integer> cache = new LRUCache<>(2);

        cache.put(1, 1);
        cache.put(2, 2);
        System.out.println(cache.get(1)); // Optional[1]
        cache.put(3, 3);
        System.out.println(cache.get(2)); // Optional.empty
        cache.put(4, 4);
        System.out.println(cache.get(1)); // Optional.empty
        System.out.println(cache.get(3)); // Optional[3]
        System.out.println(cache.get(4)); // Optional[4]
    }
}
