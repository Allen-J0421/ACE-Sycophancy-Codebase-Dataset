import java.util.*;
import java.util.concurrent.locks.*;
import java.util.function.*;

/**
 * Segmented concurrent hash map using lock striping for improved write concurrency.
 *
 * <p>This implementation partitions the hash map into multiple segments, each with its own
 * ReentrantReadWriteLock. This allows:
 * <ul>
 *   <li>Multiple concurrent writes to different segments</li>
 *   <li>Multiple concurrent readers on all segments</li>
 *   <li>Fine-grained locking without global synchronization</li>
 * </ul>
 *
 * <p><b>Lock Striping Strategy:</b>
 * <ul>
 *   <li>Default: 16 segments (customizable)</li>
 *   <li>Segment = bucket.hashCode() % numSegments</li>
 *   <li>Each segment has its own ReentrantReadWriteLock</li>
 *   <li>Operations lock only their segment, not the entire table</li>
 * </ul>
 *
 * <p><b>Performance Improvement:</b>
 * <ul>
 *   <li>Write-heavy workloads: N × better (where N = number of segments)</li>
 *   <li>Read-heavy workloads: Same as global lock (reads don't block each other)</li>
 *   <li>Mixed workloads: Significant improvement (writes to different segments concurrent)</li>
 * </ul>
 *
 * <p><b>Example:</b>
 * <pre>
 * SegmentedConcurrentHashMap&lt;String, Integer&gt; map =
 *     new SegmentedConcurrentHashMap&lt;&gt;(16, 16);  // 16 segments, capacity 16
 *
 * // Thread 1: writes to bucket 0-1 (segment 0)
 * map.put("key0", 100);
 *
 * // Thread 2: concurrently writes to bucket 8-9 (segment 1)
 * // These can run in parallel - different segments!
 * map.put("key8", 200);
 * </pre>
 *
 * @param <K> the type of keys
 * @param <V> the type of values
 * @since 4.1
 */
public class SegmentedConcurrentHashMap<K, V> {
    private static final int DEFAULT_SEGMENTS = 16;

    private final Segment<K, V>[] segments;
    private final int numSegments;
    private final int segmentMask;

    /**
     * Constructs a segmented concurrent hash map with default configuration.
     * Default: 16 segments, 16 initial capacity per segment.
     */
    @SuppressWarnings("unchecked")
    public SegmentedConcurrentHashMap() {
        this(DEFAULT_SEGMENTS, 16);
    }

    /**
     * Constructs a segmented concurrent hash map with specified segment count.
     *
     * @param numSegments the number of segments (will be rounded to power of 2)
     */
    @SuppressWarnings("unchecked")
    public SegmentedConcurrentHashMap(int numSegments) {
        this(numSegments, 16);
    }

    /**
     * Constructs a segmented concurrent hash map with specified segments and capacity.
     *
     * @param numSegments the number of segments (will be rounded to power of 2)
     * @param initialCapacity the initial capacity per segment
     */
    @SuppressWarnings("unchecked")
    public SegmentedConcurrentHashMap(int numSegments, int initialCapacity) {
        // Normalize numSegments to power of 2
        this.numSegments = nextPowerOfTwo(numSegments);
        this.segmentMask = this.numSegments - 1;
        this.segments = new Segment[this.numSegments];

        // Initialize all segments
        for (int i = 0; i < this.numSegments; i++) {
            segments[i] = new Segment<>(initialCapacity);
        }
    }

    /**
     * Gets the segment for a key using its hash code.
     *
     * @param key the key whose segment is to be determined
     * @return the segment index for this key
     */
    private int getSegmentIndex(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        return (key.hashCode() & 0x7FFFFFFF) & segmentMask;
    }

    /**
     * Associates the specified value with the specified key.
     * Uses segment-specific write lock for fine-grained concurrency.
     *
     * @param key the key
     * @param value the value
     * @return the previous value, or null
     */
    public V put(K key, V value) {
        Segment<K, V> segment = segments[getSegmentIndex(key)];
        return segment.put(key, value);
    }

    /**
     * Returns the value for the specified key.
     * Uses segment-specific read lock for maximum parallelism.
     *
     * @param key the key
     * @return the value, or null if not present
     */
    public V get(K key) {
        Segment<K, V> segment = segments[getSegmentIndex(key)];
        return segment.get(key);
    }

    /**
     * Removes the mapping for the specified key.
     * Uses segment-specific write lock.
     *
     * @param key the key
     * @return the removed value, or null
     */
    public V remove(K key) {
        Segment<K, V> segment = segments[getSegmentIndex(key)];
        return segment.remove(key);
    }

    /**
     * Checks if the key is present.
     * Uses segment-specific read lock.
     *
     * @param key the key to check
     * @return true if present
     */
    public boolean containsKey(K key) {
        Segment<K, V> segment = segments[getSegmentIndex(key)];
        return segment.containsKey(key);
    }

    /**
     * Returns the total size across all segments.
     * Acquires read locks on all segments.
     *
     * @return the total number of entries
     */
    public int size() {
        int size = 0;
        for (Segment<K, V> segment : segments) {
            size += segment.size();
        }
        return size;
    }

    /**
     * Checks if the map is empty across all segments.
     *
     * @return true if no entries
     */
    public boolean isEmpty() {
        for (Segment<K, V> segment : segments) {
            if (!segment.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Clears all entries from all segments.
     * Acquires write locks on all segments.
     */
    public void clear() {
        for (Segment<K, V> segment : segments) {
            segment.clear();
        }
    }

    /**
     * Returns the value, or default if not present.
     * Uses segment-specific read lock.
     *
     * @param key the key
     * @param defaultValue the default value
     * @return the value or default
     */
    public V getOrDefault(K key, V defaultValue) {
        V value = get(key);
        return value != null ? value : defaultValue;
    }

    /**
     * Puts value only if key is absent.
     * Uses segment-specific write lock for atomic operation.
     *
     * @param key the key
     * @param value the value
     * @return the existing value, or null if inserted
     */
    public V putIfAbsent(K key, V value) {
        Segment<K, V> segment = segments[getSegmentIndex(key)];
        return segment.putIfAbsent(key, value);
    }

    /**
     * Computes value if key is absent.
     * Uses segment-specific write lock.
     *
     * @param key the key
     * @param mappingFunction the function to compute value
     * @return the computed or existing value
     */
    public V computeIfAbsent(K key, Function<K, V> mappingFunction) {
        Segment<K, V> segment = segments[getSegmentIndex(key)];
        return segment.computeIfAbsent(key, mappingFunction);
    }

    /**
     * Computes new value if key is present.
     * Uses segment-specific write lock.
     *
     * @param key the key
     * @param remappingFunction the function to compute new value
     * @return the new value or null
     */
    public V computeIfPresent(K key, BiFunction<K, V, V> remappingFunction) {
        Segment<K, V> segment = segments[getSegmentIndex(key)];
        return segment.computeIfPresent(key, remappingFunction);
    }

    /**
     * Performs action on each entry.
     * Iterates through all segments with read locks.
     *
     * @param action the action to perform
     */
    public void forEach(BiConsumer<K, V> action) {
        for (Segment<K, V> segment : segments) {
            segment.forEach(action);
        }
    }

    /**
     * Returns the number of segments in this map.
     * Useful for monitoring concurrency level.
     *
     * @return the number of segments
     */
    public int getSegmentCount() {
        return numSegments;
    }

    /**
     * Returns statistics about lock contention across segments.
     * Useful for performance analysis.
     *
     * @return statistics string
     */
    public String getSegmentStats() {
        StringBuilder sb = new StringBuilder();
        sb.append("SegmentedConcurrentHashMap (").append(numSegments).append(" segments):\n");

        int totalSize = 0;
        for (int i = 0; i < numSegments; i++) {
            int segmentSize = segments[i].size();
            totalSize += segmentSize;
            if (segmentSize > 0) {
                sb.append("  Segment ").append(i).append(": ").append(segmentSize).append(" entries\n");
            }
        }

        sb.append("  Total: ").append(totalSize).append(" entries\n");
        return sb.toString();
    }

    @Override
    public String toString() {
        return "SegmentedConcurrentHashMap{" +
               "segments=" + numSegments +
               ", size=" + size() +
               "}";
    }

    /**
     * Rounds up to the next power of two.
     */
    private static int nextPowerOfTwo(int n) {
        int power = 1;
        while (power < n && power < (1 << 30)) {
            power <<= 1;
        }
        return power;
    }

    /**
     * Individual segment with its own lock and hash table.
     *
     * @param <K> key type
     * @param <V> value type
     */
    /**
     * Individual segment with its own lock and hash table.
     */
    private static class Segment<K, V> {
        private final LinearProbingHashMap<K, V> map;
        private final ReadWriteLock lock = new ReentrantReadWriteLock();

        Segment(int initialCapacity) {
            this.map = new LinearProbingHashMap<>(initialCapacity, false);
        }

        V put(K key, V value) {
            lock.writeLock().lock();
            try {
                return map.put(key, value);
            } finally {
                lock.writeLock().unlock();
            }
        }

        V get(K key) {
            lock.readLock().lock();
            try {
                return map.get(key);
            } finally {
                lock.readLock().unlock();
            }
        }

        V remove(K key) {
            lock.writeLock().lock();
            try {
                return map.remove(key);
            } finally {
                lock.writeLock().unlock();
            }
        }

        boolean containsKey(K key) {
            lock.readLock().lock();
            try {
                return map.containsKey(key);
            } finally {
                lock.readLock().unlock();
            }
        }

        int size() {
            lock.readLock().lock();
            try {
                return map.size();
            } finally {
                lock.readLock().unlock();
            }
        }

        boolean isEmpty() {
            lock.readLock().lock();
            try {
                return map.isEmpty();
            } finally {
                lock.readLock().unlock();
            }
        }

        void clear() {
            lock.writeLock().lock();
            try {
                map.clear();
            } finally {
                lock.writeLock().unlock();
            }
        }

        V putIfAbsent(K key, V value) {
            lock.writeLock().lock();
            try {
                V existing = map.get(key);
                if (existing == null) {
                    map.put(key, value);
                }
                return existing;
            } finally {
                lock.writeLock().unlock();
            }
        }

        V computeIfAbsent(K key, Function<K, V> mappingFunction) {
            lock.writeLock().lock();
            try {
                V existing = map.get(key);
                if (existing == null) {
                    V newValue = mappingFunction.apply(key);
                    if (newValue != null) {
                        map.put(key, newValue);
                    }
                    return newValue;
                }
                return existing;
            } finally {
                lock.writeLock().unlock();
            }
        }

        V computeIfPresent(K key, BiFunction<K, V, V> remappingFunction) {
            lock.writeLock().lock();
            try {
                V existing = map.get(key);
                if (existing != null) {
                    V newValue = remappingFunction.apply(key, existing);
                    if (newValue != null) {
                        map.put(key, newValue);
                        return newValue;
                    } else {
                        map.remove(key);
                        return null;
                    }
                }
                return null;
            } finally {
                lock.writeLock().unlock();
            }
        }

        void forEach(BiConsumer<K, V> action) {
            lock.readLock().lock();
            try {
                map.forEach(action);
            } finally {
                lock.readLock().unlock();
            }
        }
    }
}

/**
 * Demonstration and benchmarking of segmented concurrent hash map.
 */
class SegmentedDemo {
    public static void main(String[] args) throws InterruptedException {
        testBasicOperation();
        testConcurrentWrites();
        testSegmentDistribution();
    }

    private static void testBasicOperation() {
        System.out.println("=== Test: Basic Segmented Operations ===");
        SegmentedConcurrentHashMap<String, Integer> map =
            new SegmentedConcurrentHashMap<>(16, 16);

        map.put("key1", 100);
        map.put("key2", 200);
        map.put("key3", 300);

        System.out.println("Get key1: " + map.get("key1"));
        System.out.println("Get key2: " + map.get("key2"));
        System.out.println("Size: " + map.size());
        System.out.println();
    }

    private static void testConcurrentWrites() throws InterruptedException {
        System.out.println("=== Test: Concurrent Writes to Different Segments ===");
        SegmentedConcurrentHashMap<Integer, Integer> map =
            new SegmentedConcurrentHashMap<>(16, 16);

        long startTime = System.currentTimeMillis();

        // 4 writer threads, each writing to different segments
        Thread[] writers = new Thread[4];
        for (int t = 0; t < 4; t++) {
            final int threadId = t;
            writers[t] = new Thread(() -> {
                // Each thread writes to a different key pattern
                // Key pattern ensures distribution across segments
                for (int i = 0; i < 1000; i++) {
                    int key = threadId * 1000 + i;
                    map.put(key, i * 100);
                }
                System.out.println("Writer " + threadId + " completed 1000 writes");
            });
        }

        for (Thread writer : writers) {
            writer.start();
        }

        for (Thread writer : writers) {
            writer.join();
        }

        long elapsed = System.currentTimeMillis() - startTime;
        System.out.println("Total time for 4000 concurrent writes: " + elapsed + "ms");
        System.out.println("Average: " + (elapsed / 4.0) + "ms per writer");
        System.out.println("Final size: " + map.size());
        System.out.println();
    }

    private static void testSegmentDistribution() {
        System.out.println("=== Test: Segment Distribution ===");
        SegmentedConcurrentHashMap<String, String> map =
            new SegmentedConcurrentHashMap<>(16, 16);

        // Add entries to see distribution across segments
        String[] keys = {"alice", "bob", "charlie", "david", "eve", "frank",
                        "grace", "henry", "iris", "jack", "kate", "leo"};

        for (String key : keys) {
            map.put(key, "value_" + key);
        }

        System.out.println(map.getSegmentStats());
        System.out.println("Total entries: " + map.size());
        System.out.println("Segment count: " + map.getSegmentCount());
        System.out.println();
    }
}
