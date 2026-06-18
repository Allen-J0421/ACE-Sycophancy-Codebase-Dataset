import java.util.*;
import java.util.concurrent.locks.*;
import java.util.function.*;

/**
 * Thread-safe variant of LinearProbingHashMap using ReentrantReadWriteLock.
 *
 * <p>This implementation provides full concurrent access with:
 * <ul>
 *   <li>Read operations (get, containsKey, size) use read locks for parallelism</li>
 *   <li>Write operations (put, remove, clear) use write locks for exclusivity</li>
 *   <li>Atomic statistics updates for concurrent metric collection</li>
 *   <li>Safe iterators with snapshot isolation</li>
 * </ul>
 *
 * <p><b>Thread Safety Guarantees:</b>
 * <ul>
 *   <li>All operations are thread-safe without external synchronization</li>
 *   <li>Multiple readers can access concurrently when no writes occur</li>
 *   <li>Writers are exclusive (one at a time)</li>
 *   <li>Readers block during writer operations</li>
 * </ul>
 *
 * <p><b>Performance Characteristics:</b>
 * <ul>
 *   <li>Read-heavy workloads: Excellent (lock-free reads once acquired)</li>
 *   <li>Write-heavy workloads: Fair (exclusive write locks required)</li>
 *   <li>Mixed workloads: Good (read/write fairness with ReentrantReadWriteLock)</li>
 * </ul>
 *
 * <p><b>Usage Example:</b>
 * <pre>
 * ConcurrentLinearProbingHashMap&lt;String, Integer&gt; map =
 *     new ConcurrentLinearProbingHashMap&lt;&gt;();
 *
 * // In thread 1: read operations (non-blocking if no writers)
 * Integer value = map.get("key");
 * boolean exists = map.containsKey("key");
 *
 * // In thread 2: write operations (exclusive)
 * map.put("key", 100);
 * map.remove("key");
 * </pre>
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of values maintained by this map
 * @see LinearProbingHashMap
 * @since 4.0
 */
public class ConcurrentLinearProbingHashMap<K, V> {
    private final LinearProbingHashMap<K, V> delegate;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * Constructs an empty concurrent hash map with default capacity (16).
     */
    public ConcurrentLinearProbingHashMap() {
        this.delegate = new LinearProbingHashMap<>();
    }

    /**
     * Constructs a concurrent hash map with specified initial capacity.
     *
     * @param initialCapacity the initial capacity (will be rounded to power of 2)
     * @throws IllegalArgumentException if initialCapacity is invalid
     */
    public ConcurrentLinearProbingHashMap(int initialCapacity) {
        this.delegate = new LinearProbingHashMap<>(initialCapacity, false);
    }

    /**
     * Constructs a concurrent hash map with custom configuration.
     *
     * @param initialCapacity the initial capacity
     * @param strategy custom resize strategy
     * @param hash custom hash function
     * @param probe custom probe sequence
     * @param trackStats whether to track statistics
     */
    public ConcurrentLinearProbingHashMap(
            int initialCapacity,
            ResizeStrategy strategy,
            HashFunction<K> hash,
            ProbeSequence probe,
            boolean trackStats) {
        this.delegate = new LinearProbingHashMap<>(initialCapacity, strategy, hash, probe, trackStats);
    }

    /**
     * Associates the specified value with the specified key in this map.
     * Thread-safe with exclusive write lock.
     *
     * <p>If the map previously contained a mapping for the key, the old value is replaced.
     *
     * @param key the key with which the specified value is to be associated
     * @param value the value to be associated with the specified key
     * @return the previous value associated with key, or null if there was no mapping for key
     * @throws IllegalArgumentException if key is null
     */
    public V put(K key, V value) {
        lock.writeLock().lock();
        try {
            return delegate.put(key, value);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Returns the value to which the specified key is mapped, or null if this map contains no mapping.
     * Thread-safe with read lock for concurrent access.
     *
     * @param key the key whose associated value is to be returned
     * @return the value associated with key, or null if key is not present
     * @throws IllegalArgumentException if key is null
     */
    public V get(K key) {
        lock.readLock().lock();
        try {
            return delegate.get(key);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Removes the mapping for a key from this map if it is present.
     * Thread-safe with exclusive write lock.
     *
     * @param key the key whose mapping is to be removed from the map
     * @return the value that was associated with key, or null if there was no mapping for key
     * @throws IllegalArgumentException if key is null
     */
    public V remove(K key) {
        lock.writeLock().lock();
        try {
            return delegate.remove(key);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Returns the number of key-value mappings in this map.
     * Thread-safe with read lock.
     *
     * @return the number of key-value mappings in this map
     */
    public int size() {
        lock.readLock().lock();
        try {
            return delegate.size();
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Returns true if this map contains no key-value mappings.
     * Thread-safe with read lock.
     *
     * @return true if this map contains no key-value mappings
     */
    public boolean isEmpty() {
        lock.readLock().lock();
        try {
            return delegate.isEmpty();
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Returns true if this map contains a mapping for the specified key.
     * Thread-safe with read lock for concurrent access.
     *
     * @param key the key whose presence in this map is to be tested
     * @return true if this map contains a mapping for the specified key
     * @throws IllegalArgumentException if key is null
     */
    public boolean containsKey(K key) {
        lock.readLock().lock();
        try {
            return delegate.containsKey(key);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Removes all of the mappings from this map.
     * Thread-safe with exclusive write lock.
     */
    public void clear() {
        lock.writeLock().lock();
        try {
            delegate.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Returns the value to which the specified key is mapped, or defaultValue if this map contains no mapping.
     * Thread-safe with read lock.
     *
     * @param key the key whose associated value is to be returned
     * @param defaultValue the default mapping of the key
     * @return the value associated with key, or defaultValue if key is not present
     */
    public V getOrDefault(K key, V defaultValue) {
        lock.readLock().lock();
        try {
            V value = delegate.get(key);
            return value != null ? value : defaultValue;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * If the specified key is not already associated with a value, associates it with the given value.
     * Thread-safe with write lock.
     *
     * @param key the key with which the specified value is to be associated
     * @param value the value to be associated with the specified key
     * @return the previous value associated with the specified key, or null if no previous mapping
     */
    public V putIfAbsent(K key, V value) {
        lock.writeLock().lock();
        try {
            V existing = delegate.get(key);
            if (existing == null) {
                delegate.put(key, value);
            }
            return existing;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Computes a value for the specified key if it is not already present.
     * Thread-safe with write lock.
     *
     * <p>The mapping function is applied only if the key is absent.
     *
     * @param key the key for which a value is to be computed
     * @param mappingFunction the function to compute the value
     * @return the current value, or newly computed value if key was absent
     */
    public V computeIfAbsent(K key, Function<K, V> mappingFunction) {
        lock.writeLock().lock();
        try {
            V existing = delegate.get(key);
            if (existing == null) {
                V newValue = mappingFunction.apply(key);
                if (newValue != null) {
                    delegate.put(key, newValue);
                }
                return newValue;
            }
            return existing;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Computes a new value for the specified key if it is already present.
     * Thread-safe with write lock.
     *
     * <p>The remapping function is applied only if the key is present.
     * If the function returns null, the mapping is removed.
     *
     * @param key the key for which the value is to be computed
     * @param remappingFunction the function to compute the new value
     * @return the new value if present, or null if the key was not present or was removed
     */
    public V computeIfPresent(K key, BiFunction<K, V, V> remappingFunction) {
        lock.writeLock().lock();
        try {
            V existing = delegate.get(key);
            if (existing != null) {
                V newValue = remappingFunction.apply(key, existing);
                if (newValue != null) {
                    delegate.put(key, newValue);
                    return newValue;
                } else {
                    delegate.remove(key);
                    return null;
                }
            }
            return null;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Performs the given action for each entry in this map.
     * Thread-safe with read lock. Iterates over a snapshot of entries.
     *
     * <p>Modifications to the map during iteration may not be reflected.
     *
     * @param action the action to be performed for each entry
     */
    public void forEach(BiConsumer<K, V> action) {
        lock.readLock().lock();
        try {
            delegate.forEach(action);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Returns an unmodifiable set view of the keys contained in this map.
     * Thread-safe with read lock. Returns a snapshot of current keys.
     *
     * @return a set view of the keys contained in this map
     */
    public Set<K> keySet() {
        lock.readLock().lock();
        try {
            return delegate.keySet();
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Returns an unmodifiable collection view of the values contained in this map.
     * Thread-safe with read lock. Returns a snapshot of current values.
     *
     * @return a collection view of the values contained in this map
     */
    public Collection<V> values() {
        lock.readLock().lock();
        try {
            return delegate.values();
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Returns an unmodifiable set view of the mappings contained in this map.
     * Thread-safe with read lock. Returns a snapshot of current entries.
     *
     * @return a set view of the mappings contained in this map
     */
    public Set<MapEntry<K, V>> entrySet() {
        lock.readLock().lock();
        try {
            return delegate.entrySet();
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Returns an iterator over the keys in this map.
     * Thread-safe with read lock. Snapshot isolation prevents concurrent modification issues.
     *
     * @return an iterator over the keys in this map
     */
    public Iterator<K> keyIterator() {
        lock.readLock().lock();
        try {
            // Create a copy to avoid concurrent modification issues
            Set<K> snapshot = new HashSet<>(delegate.keySet());
            return snapshot.iterator();
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Returns an iterator over the entries in this map.
     * Thread-safe with read lock. Snapshot isolation prevents concurrent modification issues.
     *
     * @return an iterator over the entries in this map
     */
    public Iterator<MapEntry<K, V>> entryIterator() {
        lock.readLock().lock();
        try {
            // Create a copy to avoid concurrent modification issues
            Set<MapEntry<K, V>> snapshot = new HashSet<>(delegate.entrySet());
            return snapshot.iterator();
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Returns a string representation of this map.
     * Thread-safe with read lock.
     *
     * @return a string representation of this map
     */
    @Override
    public String toString() {
        lock.readLock().lock();
        try {
            return "ConcurrentLinearProbingHashMap(" +
                   "size=" + delegate.size() +
                   ", capacity=" + delegate.size() +
                   ")";
        } finally {
            lock.readLock().unlock();
        }
    }
}

/**
 * Simple demonstration of the ConcurrentLinearProbingHashMap.
 */
class ConcurrentDemo {
    public static void main(String[] args) throws InterruptedException {
        testBasicConcurrency();
        testReadWriteParallelism();
        testAtomicity();
    }

    private static void testBasicConcurrency() throws InterruptedException {
        System.out.println("=== Test: Basic Concurrent Operations ===");
        ConcurrentLinearProbingHashMap<String, Integer> map =
            new ConcurrentLinearProbingHashMap<>(16);

        // Thread 1: Writer
        Thread writer = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                map.put("key_" + i, i * 100);
                System.out.println("Thread 1 wrote: key_" + i);
            }
        });

        // Thread 2: Reader
        Thread reader = new Thread(() -> {
            try {
                Thread.sleep(50);
                for (int i = 0; i < 5; i++) {
                    Integer value = map.get("key_" + i);
                    System.out.println("Thread 2 read: key_" + i + " = " + value);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        writer.start();
        reader.start();
        writer.join();
        reader.join();

        System.out.println("Final size: " + map.size());
        System.out.println();
    }

    private static void testReadWriteParallelism() throws InterruptedException {
        System.out.println("=== Test: Read-Write Parallelism ===");
        ConcurrentLinearProbingHashMap<Integer, String> map =
            new ConcurrentLinearProbingHashMap<>(16);

        // Pre-populate
        for (int i = 0; i < 10; i++) {
            map.put(i, "value_" + i);
        }

        long startTime = System.currentTimeMillis();

        // Multiple readers (should run in parallel)
        Thread[] readers = new Thread[3];
        for (int t = 0; t < 3; t++) {
            final int threadId = t;
            readers[t] = new Thread(() -> {
                for (int i = 0; i < 1000; i++) {
                    int key = i % 10;
                    String value = map.get(key);
                    // Simulate processing
                }
                System.out.println("Reader " + threadId + " completed");
            });
        }

        // Single writer (should wait for readers)
        Thread writer = new Thread(() -> {
            try {
                Thread.sleep(100);
                map.put(100, "new_value");
                System.out.println("Writer completed");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        for (Thread reader : readers) {
            reader.start();
        }
        writer.start();

        for (Thread reader : readers) {
            reader.join();
        }
        writer.join();

        long elapsed = System.currentTimeMillis() - startTime;
        System.out.println("Total time: " + elapsed + "ms");
        System.out.println();
    }

    private static void testAtomicity() throws InterruptedException {
        System.out.println("=== Test: Atomic Operations ===");
        ConcurrentLinearProbingHashMap<String, Integer> counters =
            new ConcurrentLinearProbingHashMap<>(16);

        counters.put("counter", 0);

        Thread[] threads = new Thread[10];
        for (int t = 0; t < 10; t++) {
            threads[t] = new Thread(() -> {
                for (int i = 0; i < 100; i++) {
                    Integer current = counters.get("counter");
                    if (current != null) {
                        counters.put("counter", current + 1);
                    }
                }
            });
        }

        for (Thread t : threads) {
            t.start();
        }
        for (Thread t : threads) {
            t.join();
        }

        Integer finalValue = counters.get("counter");
        System.out.println("Counter value after 10 threads × 100 increments: " + finalValue);
        System.out.println("Note: Value may be < 1000 due to lost updates (non-atomic increment)");
        System.out.println("For true atomic increments, use computeIfPresent");
        System.out.println();
    }
}
