import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

interface Cache<K, V> {
    Optional<V> get(K key);
    void put(K key, V value);
    boolean containsKey(K key);
    void invalidate(K key);
    void clear();
    int size();

    default boolean isEmpty() {
        return size() == 0;
    }

    default V getOrDefault(K key, V defaultValue) {
        return get(key).orElse(defaultValue);
    }

    // Uses containsKey as the guard rather than get() so that a stored null value
    // does not incorrectly trigger the loader.
    default V computeIfAbsent(K key, Function<? super K, ? extends V> loader) {
        if (!containsKey(key)) {
            put(key, loader.apply(key));
        }
        return get(key).orElse(null);
    }
}

class LRUCache<K, V> implements Cache<K, V> {

    private static class EvictingLinkedHashMap<K, V> extends LinkedHashMap<K, V> {
        private static final long serialVersionUID = 1L;
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

    private LRUCache(int capacity) {
        this.store = new EvictingLinkedHashMap<>(capacity);
    }

    static <K, V> LRUCache<K, V> create(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive, got: " + capacity);
        }
        return new LRUCache<>(capacity);
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
    public boolean containsKey(K key) {
        return store.containsKey(key);
    }

    @Override
    public void invalidate(K key) {
        store.remove(key);
    }

    @Override
    public void clear() {
        store.clear();
    }

    @Override
    public int size() {
        return store.size();
    }
}

class SynchronizedCache<K, V> implements Cache<K, V> {

    private final Cache<K, V> delegate;

    private SynchronizedCache(Cache<K, V> delegate) {
        this.delegate = delegate;
    }

    static <K, V> Cache<K, V> wrap(Cache<K, V> delegate) {
        return new SynchronizedCache<>(delegate);
    }

    @Override public synchronized Optional<V> get(K key)          { return delegate.get(key); }
    @Override public synchronized void put(K key, V value)        { delegate.put(key, value); }
    @Override public synchronized boolean containsKey(K key)      { return delegate.containsKey(key); }
    @Override public synchronized void invalidate(K key)          { delegate.invalidate(key); }
    @Override public synchronized void clear()                    { delegate.clear(); }
    @Override public synchronized int size()                      { return delegate.size(); }

    // Overridden to hold the lock across all three steps of the default implementation,
    // making the check-then-act sequence atomic.
    @Override
    public synchronized V computeIfAbsent(K key, Function<? super K, ? extends V> loader) {
        if (!containsKey(key)) {
            put(key, loader.apply(key));
        }
        return get(key).orElse(null);
    }
}

class Main {
    public static void main(String[] args) {
        Cache<Integer, Integer> cache = LRUCache.create(2);

        cache.put(1, 1);
        cache.put(2, 2);
        System.out.println(cache.get(1));              // Optional[1]
        cache.put(3, 3);
        System.out.println(cache.get(2));              // Optional.empty  (evicted)
        cache.put(4, 4);
        System.out.println(cache.get(1));              // Optional.empty  (evicted)
        System.out.println(cache.get(3));              // Optional[3]
        System.out.println(cache.get(4));              // Optional[4]

        System.out.println(cache.containsKey(3));      // true
        cache.invalidate(3);
        System.out.println(cache.containsKey(3));      // false
        System.out.println(cache.getOrDefault(3, -1)); // -1
        System.out.println(cache.size());              // 1
        System.out.println(cache.isEmpty());           // false

        // computeIfAbsent: loader runs on miss, skipped on hit
        System.out.println(cache.computeIfAbsent(5, k -> k * 10)); // 50  (miss: computed)
        System.out.println(cache.computeIfAbsent(5, k -> k * 99)); // 50  (hit: loader skipped)

        cache.clear();
        System.out.println(cache.isEmpty());           // true

        // Thread-safe wrapper: same Cache interface, synchronized operations
        Cache<String, String> safe = SynchronizedCache.wrap(LRUCache.create(3));
        safe.put("a", "apple");
        System.out.println(safe.computeIfAbsent("b", k -> k + "anana")); // banana
        System.out.println(safe.get("a"));                                // Optional[apple]
    }
}
