import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
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

    // Returns true if the key was absent and the value was stored.
    default boolean putIfAbsent(K key, V value) {
        if (containsKey(key)) {
            return false;
        }
        put(key, value);
        return true;
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
        private final BiConsumer<K, V> onEviction;

        EvictingLinkedHashMap(int capacity, BiConsumer<K, V> onEviction) {
            super(capacity + 1, 0.75f, true);
            this.capacity = capacity;
            this.onEviction = onEviction;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            if (size() > capacity) {
                onEviction.accept(eldest.getKey(), eldest.getValue());
                return true;
            }
            return false;
        }
    }

    private final Map<K, V> store;

    private LRUCache(int capacity, BiConsumer<K, V> onEviction) {
        this.store = new EvictingLinkedHashMap<>(capacity, onEviction);
    }

    static <K, V> LRUCache<K, V> create(int capacity) {
        return create(capacity, (k, v) -> {});
    }

    static <K, V> LRUCache<K, V> create(int capacity, BiConsumer<K, V> onEviction) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive, got: " + capacity);
        }
        Objects.requireNonNull(onEviction, "onEviction");
        return new LRUCache<>(capacity, onEviction);
    }

    @Override public Optional<V> get(K key)     { return Optional.ofNullable(store.get(key)); }
    @Override public void put(K key, V value)   { store.put(key, value); }
    @Override public boolean containsKey(K key) { return store.containsKey(key); }
    @Override public void invalidate(K key)     { store.remove(key); }
    @Override public void clear()               { store.clear(); }
    @Override public int size()                 { return store.size(); }
}

class SynchronizedCache<K, V> implements Cache<K, V> {

    private final Cache<K, V> delegate;

    private SynchronizedCache(Cache<K, V> delegate) {
        this.delegate = delegate;
    }

    static <K, V> Cache<K, V> wrap(Cache<K, V> delegate) {
        Objects.requireNonNull(delegate, "delegate");
        return new SynchronizedCache<>(delegate);
    }

    @Override public synchronized Optional<V> get(K key)          { return delegate.get(key); }
    @Override public synchronized void put(K key, V value)        { delegate.put(key, value); }
    @Override public synchronized boolean containsKey(K key)      { return delegate.containsKey(key); }
    @Override public synchronized void invalidate(K key)          { delegate.invalidate(key); }
    @Override public synchronized void clear()                    { delegate.clear(); }
    @Override public synchronized int size()                      { return delegate.size(); }

    // Both compound operations are overridden to hold the lock across all steps,
    // making each check-then-act sequence atomic. Java's reentrant monitors allow
    // the nested synchronized calls to re-enter the same lock safely.
    @Override
    public synchronized boolean putIfAbsent(K key, V value) {
        return Cache.super.putIfAbsent(key, value);
    }

    @Override
    public synchronized V computeIfAbsent(K key, Function<? super K, ? extends V> loader) {
        return Cache.super.computeIfAbsent(key, loader);
    }
}

class Main {
    public static void main(String[] args) {
        demonstrateEviction();
        demonstrateEvictionListener();
        demonstrateSynchronizedWrapper();
    }

    private static void demonstrateEviction() {
        Cache<Integer, Integer> cache = LRUCache.create(2);

        cache.put(1, 1);
        cache.put(2, 2);
        System.out.println(cache.get(1));              // Optional[1]
        cache.put(3, 3);
        System.out.println(cache.get(2));              // Optional.empty (evicted)
        cache.put(4, 4);
        System.out.println(cache.get(1));              // Optional.empty (evicted)
        System.out.println(cache.get(3));              // Optional[3]
        System.out.println(cache.get(4));              // Optional[4]

        cache.invalidate(3);
        System.out.println(cache.getOrDefault(3, -1)); // -1
        System.out.println(cache.size());              // 1
        System.out.println(cache.isEmpty());           // false

        System.out.println(cache.putIfAbsent(4, 999)); // false (4 already present)
        System.out.println(cache.putIfAbsent(9, 999)); // true  (9 was absent)
        System.out.println(cache.computeIfAbsent(9, k -> k * 10)); // 999 (hit: loader skipped)
        System.out.println(cache.computeIfAbsent(7, k -> k * 10)); // 70  (miss: computed)

        cache.clear();
        System.out.println(cache.isEmpty());           // true
    }

    private static void demonstrateEvictionListener() {
        Cache<Integer, Integer> tracked = LRUCache.create(2,
            (k, v) -> System.out.println("evicted: " + k + "=" + v));
        tracked.put(1, 10);
        tracked.put(2, 20);
        tracked.put(3, 30); // evicts 1=10
        tracked.put(4, 40); // evicts 2=20
    }

    private static void demonstrateSynchronizedWrapper() {
        Cache<String, String> safe = SynchronizedCache.wrap(LRUCache.create(3));
        safe.put("a", "apple");
        System.out.println(safe.putIfAbsent("a", "avocado")); // false (a already present)
        System.out.println(safe.computeIfAbsent("b", k -> k + "anana")); // banana
        System.out.println(safe.get("a"));                               // Optional[apple]
    }
}
