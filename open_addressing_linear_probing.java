import java.util.*;
import java.util.function.*;

// ============================================================================
// CORE DATA STRUCTURES
// ============================================================================

class HashEntry<K, V> {
    final K key;
    V value;
    boolean deleted;

    HashEntry(K key, V value) {
        this.key = key;
        this.value = value;
        this.deleted = false;
    }

    boolean isActive() {
        return !deleted;
    }

    boolean matches(K k) {
        return key != null && key.equals(k) && !deleted;
    }

    void markDeleted() {
        this.deleted = true;
    }

    void updateValue(V newValue) {
        this.value = newValue;
        this.deleted = false;
    }

    @Override
    public String toString() {
        return key + " -> " + value + (deleted ? " [DELETED]" : "");
    }
}

class HashStats {
    private long totalProbes;
    private int maxProbeDepth;
    private int collisions;
    private int resizeCount;
    private long operationCount;

    void recordProbe(int depth) {
        totalProbes++;
        maxProbeDepth = Math.max(maxProbeDepth, depth);
    }

    void recordCollision() {
        collisions++;
    }

    void recordResize() {
        resizeCount++;
    }

    void recordOperation() {
        operationCount++;
    }

    void reset() {
        totalProbes = 0;
        maxProbeDepth = 0;
        collisions = 0;
        resizeCount = 0;
        operationCount = 0;
    }

    long getTotalProbes() {
        return totalProbes;
    }

    int getMaxProbeDepth() {
        return maxProbeDepth;
    }

    int getCollisions() {
        return collisions;
    }

    int getResizeCount() {
        return resizeCount;
    }

    long getOperationCount() {
        return operationCount;
    }

    double getAverageProbeDepth() {
        return operationCount > 0 ? (double) totalProbes / operationCount : 0;
    }

    void print() {
        System.out.println("  Total Probes: " + totalProbes);
        System.out.println("  Avg Probe Depth: " + String.format("%.2f", getAverageProbeDepth()));
        System.out.println("  Max Probe Depth: " + maxProbeDepth);
        System.out.println("  Collisions: " + collisions);
        System.out.println("  Resize Count: " + resizeCount);
        System.out.println("  Operations: " + operationCount);
    }
}

// ============================================================================
// INTERFACES AND STRATEGIES
// ============================================================================

interface MapEntry<K, V> {
    K getKey();
    V getValue();
    V setValue(V value);

    @Override
    boolean equals(Object o);

    @Override
    int hashCode();
}

interface HashMap<K, V> {
    V put(K key, V value);
    V get(K key);
    V remove(K key);
    int size();
    boolean isEmpty();
    boolean containsKey(K key);
    void clear();
    Set<K> keySet();
    Collection<V> values();
    Set<MapEntry<K, V>> entrySet();

    V getOrDefault(K key, V defaultValue);
    V putIfAbsent(K key, V value);
    V computeIfAbsent(K key, Function<K, V> mapping);
    V computeIfPresent(K key, BiFunction<K, V, V> remapping);
    void forEach(BiConsumer<K, V> action);
}

interface ResizeStrategy {
    boolean shouldExpand(int size, int deletedCount, int capacity);
    boolean shouldShrink(int size, int capacity);
    int nextCapacity(int currentCapacity, boolean expand);
    int getMinCapacity();
    int getMaxCapacity();
}

class DefaultResizeStrategy implements ResizeStrategy {
    private static final int MIN_CAPACITY = 16;
    private static final int MAX_CAPACITY = 1 << 30;
    private static final float UPPER_LOAD_FACTOR = 0.75f;
    private static final float LOWER_LOAD_FACTOR = 0.25f;

    @Override
    public boolean shouldExpand(int size, int deletedCount, int capacity) {
        return (size + deletedCount) >= capacity * UPPER_LOAD_FACTOR;
    }

    @Override
    public boolean shouldShrink(int size, int capacity) {
        return capacity > MIN_CAPACITY && size <= capacity * LOWER_LOAD_FACTOR;
    }

    @Override
    public int nextCapacity(int currentCapacity, boolean expand) {
        int newCapacity = expand ? currentCapacity * 2 : currentCapacity / 2;
        return Math.max(Math.min(newCapacity, MAX_CAPACITY), MIN_CAPACITY);
    }

    @Override
    public int getMinCapacity() {
        return MIN_CAPACITY;
    }

    @Override
    public int getMaxCapacity() {
        return MAX_CAPACITY;
    }

    public float getUpperLoadFactor() {
        return UPPER_LOAD_FACTOR;
    }

    public float getLowerLoadFactor() {
        return LOWER_LOAD_FACTOR;
    }
}

interface HashFunction<K> {
    int hash(K key, int capacity);
}

class DefaultHashFunction<K> implements HashFunction<K> {
    @Override
    public int hash(K key, int capacity) {
        if (key == null) return 0;
        int h = key.hashCode();
        h = h ^ (h >>> 16);
        return Math.abs(h % capacity);
    }

    @Override
    public String toString() {
        return "DefaultHashFunction";
    }
}

interface ProbeSequence {
    int nextProbe(int startIndex, int probeCount, int capacity);
}

class LinearProbeSequence implements ProbeSequence {
    @Override
    public int nextProbe(int startIndex, int probeCount, int capacity) {
        return (startIndex + probeCount) % capacity;
    }

    @Override
    public String toString() {
        return "LinearProbeSequence";
    }
}

class QuadraticProbeSequence implements ProbeSequence {
    @Override
    public int nextProbe(int startIndex, int probeCount, int capacity) {
        int c1 = 1;
        int c2 = 3;
        long offset = (long) c1 * probeCount + (long) c2 * probeCount * probeCount;
        return (int) ((startIndex + offset) % capacity);
    }

    @Override
    public String toString() {
        return "QuadraticProbeSequence";
    }
}

// ============================================================================
// ITERATORS AND VIEWS
// ============================================================================

class HashMapIterator<K, V> implements Iterator<K> {
    private HashEntry<K, V>[] table;
    private int currentIndex = -1;
    private int nextIndex = -1;
    private boolean removed = false;

    HashMapIterator(HashEntry<K, V>[] table) {
        this.table = table;
        advance();
    }

    private void advance() {
        nextIndex = -1;
        for (int i = currentIndex + 1; i < table.length; i++) {
            if (table[i] != null && table[i].isActive()) {
                nextIndex = i;
                break;
            }
        }
    }

    @Override
    public boolean hasNext() {
        return nextIndex >= 0;
    }

    @Override
    public K next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        currentIndex = nextIndex;
        K key = table[currentIndex].key;
        advance();
        removed = false;
        return key;
    }

    @Override
    public void remove() {
        if (currentIndex < 0 || removed) {
            throw new IllegalStateException();
        }
        table[currentIndex].markDeleted();
        removed = true;
    }
}

class HashMapEntryIterator<K, V> implements Iterator<MapEntry<K, V>> {
    private HashEntry<K, V>[] table;
    private int currentIndex = -1;
    private int nextIndex = -1;
    private boolean removed = false;

    HashMapEntryIterator(HashEntry<K, V>[] table) {
        this.table = table;
        advance();
    }

    private void advance() {
        nextIndex = -1;
        for (int i = currentIndex + 1; i < table.length; i++) {
            if (table[i] != null && table[i].isActive()) {
                nextIndex = i;
                break;
            }
        }
    }

    @Override
    public boolean hasNext() {
        return nextIndex >= 0;
    }

    @Override
    public MapEntry<K, V> next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        currentIndex = nextIndex;
        MapEntry<K, V> entry = new SimpleEntry<>(table[currentIndex].key, table[currentIndex].value);
        advance();
        removed = false;
        return entry;
    }

    @Override
    public void remove() {
        if (currentIndex < 0 || removed) {
            throw new IllegalStateException();
        }
        table[currentIndex].markDeleted();
        removed = true;
    }
}

class SimpleEntry<K, V> implements MapEntry<K, V> {
    private K key;
    private V value;

    SimpleEntry(K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public K getKey() {
        return key;
    }

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public V setValue(V value) {
        V oldValue = this.value;
        this.value = value;
        return oldValue;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MapEntry)) return false;
        MapEntry<?, ?> e = (MapEntry<?, ?>) o;
        return Objects.equals(key, e.getKey()) && Objects.equals(value, e.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }

    @Override
    public String toString() {
        return key + "=" + value;
    }
}

// ============================================================================
// MAIN HASH MAP IMPLEMENTATION
// ============================================================================

class LinearProbingHashMap<K, V> implements HashMap<K, V> {
    private HashEntry<K, V>[] table;
    private int size;
    private int capacity;
    private int deletedCount;

    private ResizeStrategy resizeStrategy;
    private HashFunction<K> hashFunction;
    private ProbeSequence probeSequence;
    private HashStats stats;
    private boolean trackStats;

    @SuppressWarnings("unchecked")
    public LinearProbingHashMap() {
        this(new DefaultResizeStrategy().getMinCapacity(), true);
    }

    @SuppressWarnings("unchecked")
    public LinearProbingHashMap(int initialCapacity, boolean trackStats) {
        this(initialCapacity, new DefaultResizeStrategy(),
             new DefaultHashFunction<>(), new LinearProbeSequence(), trackStats);
    }

    @SuppressWarnings("unchecked")
    public LinearProbingHashMap(int initialCapacity, ResizeStrategy strategy,
                               HashFunction<K> hash, ProbeSequence probe, boolean trackStats) {
        validateInitialCapacity(initialCapacity, strategy);
        this.capacity = normalizeCapacity(initialCapacity, strategy.getMaxCapacity());
        this.size = 0;
        this.deletedCount = 0;
        this.table = new HashEntry[capacity];
        this.resizeStrategy = strategy;
        this.hashFunction = hash;
        this.probeSequence = probe;
        this.trackStats = trackStats;
        this.stats = new HashStats();
    }

    @Override
    public V put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }

        if (resizeStrategy.shouldExpand(size, deletedCount, capacity)) {
            resize(resizeStrategy.nextCapacity(capacity, true));
        }

        int index = findSlot(key);
        if (index < 0) {
            throw new RuntimeException("Hash table full - probe sequence exhausted");
        }

        V oldValue = null;
        boolean isNewEntry = table[index] == null || !table[index].isActive();

        if (table[index] != null && table[index].isActive()) {
            oldValue = table[index].value;
        }

        if (table[index] != null && table[index].deleted) {
            deletedCount--;
        }

        if (table[index] == null) {
            table[index] = new HashEntry<>(key, value);
        } else {
            table[index].updateValue(value);
        }

        if (isNewEntry) {
            size++;
        }

        if (trackStats) {
            stats.recordOperation();
        }

        return oldValue;
    }

    @Override
    public V get(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }

        int index = findExisting(key);
        if (trackStats) {
            stats.recordOperation();
        }
        return index >= 0 ? table[index].value : null;
    }

    @Override
    public V remove(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }

        int index = findExisting(key);
        if (index >= 0) {
            V value = table[index].value;
            table[index].markDeleted();
            size--;
            deletedCount++;

            if (resizeStrategy.shouldShrink(size, capacity)) {
                resize(resizeStrategy.nextCapacity(capacity, false));
            }

            if (trackStats) {
                stats.recordOperation();
            }
            return value;
        }
        return null;
    }

    @Override
    public V getOrDefault(K key, V defaultValue) {
        V value = get(key);
        return value != null ? value : defaultValue;
    }

    @Override
    public V putIfAbsent(K key, V value) {
        V existing = get(key);
        if (existing == null) {
            put(key, value);
        }
        return existing;
    }

    @Override
    public V computeIfAbsent(K key, Function<K, V> mapping) {
        V existing = get(key);
        if (existing == null) {
            V newValue = mapping.apply(key);
            if (newValue != null) {
                put(key, newValue);
            }
            return newValue;
        }
        return existing;
    }

    @Override
    public V computeIfPresent(K key, BiFunction<K, V, V> remapping) {
        V existing = get(key);
        if (existing != null) {
            V newValue = remapping.apply(key, existing);
            if (newValue != null) {
                put(key, newValue);
                return newValue;
            } else {
                remove(key);
                return null;
            }
        }
        return null;
    }

    @Override
    public void forEach(BiConsumer<K, V> action) {
        for (HashEntry<K, V> entry : table) {
            if (entry != null && entry.isActive()) {
                action.accept(entry.key, entry.value);
            }
        }
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean containsKey(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        return findExisting(key) >= 0;
    }

    @Override
    public void clear() {
        size = 0;
        deletedCount = 0;
        @SuppressWarnings("unchecked")
        HashEntry<K, V>[] newTable = new HashEntry[capacity];
        table = newTable;
        stats.reset();
    }

    @Override
    public Set<K> keySet() {
        Set<K> keys = new HashSet<>();
        for (HashEntry<K, V> entry : table) {
            if (entry != null && entry.isActive()) {
                keys.add(entry.key);
            }
        }
        return Collections.unmodifiableSet(keys);
    }

    @Override
    public Collection<V> values() {
        Collection<V> vals = new ArrayList<>();
        for (HashEntry<K, V> entry : table) {
            if (entry != null && entry.isActive()) {
                vals.add(entry.value);
            }
        }
        return Collections.unmodifiableCollection(vals);
    }

    @Override
    public Set<MapEntry<K, V>> entrySet() {
        Set<MapEntry<K, V>> entries = new HashSet<>();
        for (HashEntry<K, V> entry : table) {
            if (entry != null && entry.isActive()) {
                entries.add(new SimpleEntry<>(entry.key, entry.value));
            }
        }
        return Collections.unmodifiableSet(entries);
    }

    public Iterator<K> keyIterator() {
        return new HashMapIterator<>(table);
    }

    public Iterator<MapEntry<K, V>> entryIterator() {
        return new HashMapEntryIterator<>(table);
    }

    public void printStats() {
        if (trackStats) {
            System.out.println("Hash Table Statistics:");
            System.out.println("  Size: " + size + "/" + capacity);
            float loadFactor = (float) size / capacity;
            System.out.println("  Load Factor: " + String.format("%.2f%%", loadFactor * 100));
            System.out.println("  Hash Function: " + hashFunction);
            System.out.println("  Probe Sequence: " + probeSequence);
            stats.print();
        }
    }

    public void display() {
        display(false);
    }

    public void display(boolean showEmpty) {
        System.out.println("=== Hash Table ===");
        int shown = 0;
        for (int i = 0; i < capacity; i++) {
            HashEntry<K, V> entry = table[i];
            if (entry == null) {
                if (showEmpty) {
                    System.out.println("[" + i + "] EMPTY");
                }
            } else if (entry.isActive()) {
                System.out.println("[" + i + "] " + entry);
                shown++;
            } else {
                System.out.println("[" + i + "] " + entry);
                shown++;
            }
        }
        System.out.println("Total shown: " + shown + ", Size: " + size +
                          ", Capacity: " + capacity + ", Deleted: " + deletedCount);
    }

    private int findSlot(K key) {
        int startIndex = hashFunction.hash(key, capacity);
        int firstDeleted = -1;

        for (int i = 0; i < capacity; i++) {
            int probeIndex = probeSequence.nextProbe(startIndex, i, capacity);
            if (probeIndex < 0 || probeIndex >= capacity) {
                probeIndex = probeIndex % capacity;
                if (probeIndex < 0) probeIndex += capacity;
            }

            HashEntry<K, V> entry = table[probeIndex];

            if (trackStats && i > 0) {
                stats.recordProbe(i);
            }

            if (entry == null) {
                return firstDeleted >= 0 ? firstDeleted : probeIndex;
            }
            if (entry.matches(key)) {
                return probeIndex;
            }
            if (entry.deleted && firstDeleted < 0) {
                firstDeleted = probeIndex;
                if (trackStats) stats.recordCollision();
            }
        }
        return -1;
    }

    private int findExisting(K key) {
        int startIndex = hashFunction.hash(key, capacity);

        for (int i = 0; i < capacity; i++) {
            int probeIndex = probeSequence.nextProbe(startIndex, i, capacity);
            if (probeIndex < 0 || probeIndex >= capacity) {
                probeIndex = probeIndex % capacity;
                if (probeIndex < 0) probeIndex += capacity;
            }

            HashEntry<K, V> entry = table[probeIndex];

            if (trackStats && i > 0) {
                stats.recordProbe(i);
            }

            if (entry == null) {
                return -1;
            }
            if (entry.matches(key)) {
                return probeIndex;
            }
        }
        return -1;
    }

    private void resize(int newCapacity) {
        if (newCapacity == capacity) {
            return;
        }

        @SuppressWarnings("unchecked")
        HashEntry<K, V>[] oldTable = table;
        capacity = newCapacity;
        @SuppressWarnings("unchecked")
        HashEntry<K, V>[] newTable = new HashEntry[capacity];
        table = newTable;
        int oldSize = size;
        size = 0;
        deletedCount = 0;

        if (trackStats) {
            stats.recordResize();
        }

        for (HashEntry<K, V> entry : oldTable) {
            if (entry != null && entry.isActive()) {
                put(entry.key, entry.value);
            }
        }
    }

    private void validateInitialCapacity(int capacity, ResizeStrategy strategy) {
        if (capacity < strategy.getMinCapacity()) {
            throw new IllegalArgumentException(
                "Capacity " + capacity + " below minimum: " + strategy.getMinCapacity());
        }
        if (capacity > strategy.getMaxCapacity()) {
            throw new IllegalArgumentException(
                "Capacity " + capacity + " exceeds maximum: " + strategy.getMaxCapacity());
        }
    }

    private int normalizeCapacity(int capacity, int maxCapacity) {
        if (PowerOfTwo.isPowerOfTwo(capacity)) {
            return capacity;
        }
        return PowerOfTwo.nextPowerOfTwo(capacity, maxCapacity);
    }
}

// ============================================================================
// UTILITIES
// ============================================================================

class PowerOfTwo {
    static boolean isPowerOfTwo(int n) {
        return n > 0 && (n & (n - 1)) == 0;
    }

    static int nextPowerOfTwo(int n, int max) {
        if (n <= 0) {
            throw new IllegalArgumentException("n must be positive");
        }
        if (n > max) {
            return max;
        }
        int power = 1;
        while (power < n && power < max) {
            power <<= 1;
        }
        return power;
    }

    static int previousPowerOfTwo(int n) {
        if (n <= 1) {
            return 1;
        }
        int power = 1;
        while (power * 2 < n) {
            power <<= 1;
        }
        return power;
    }
}

class HashMapBuilder<K, V> {
    private int capacity = 16;
    private ResizeStrategy strategy = new DefaultResizeStrategy();
    private HashFunction<K> hash = new DefaultHashFunction<>();
    private ProbeSequence probe = new LinearProbeSequence();
    private boolean trackStats = false;

    public HashMapBuilder<K, V> capacity(int capacity) {
        this.capacity = capacity;
        return this;
    }

    public HashMapBuilder<K, V> strategy(ResizeStrategy strategy) {
        this.strategy = strategy;
        return this;
    }

    public HashMapBuilder<K, V> hashFunction(HashFunction<K> hash) {
        this.hash = hash;
        return this;
    }

    public HashMapBuilder<K, V> probeSequence(ProbeSequence probe) {
        this.probe = probe;
        return this;
    }

    public HashMapBuilder<K, V> trackStats(boolean trackStats) {
        this.trackStats = trackStats;
        return this;
    }

    public LinearProbingHashMap<K, V> build() {
        return new LinearProbingHashMap<>(capacity, strategy, hash, probe, trackStats);
    }
}

// ============================================================================
// TESTING
// ============================================================================

class Demo {
    public static void main(String[] args) {
        testIterators();
        testEntrySet();
        testFunctionalOperations();
        testForEach();
        System.out.println("\n✓ All tests completed successfully!");
    }

    private static void testIterators() {
        System.out.println("=== Test: Iterators ===");
        LinearProbingHashMap<String, Integer> map = new LinearProbingHashMap<>(16, false);

        map.put("one", 1);
        map.put("two", 2);
        map.put("three", 3);

        System.out.println("Keys via iterator:");
        Iterator<String> keyIter = map.keyIterator();
        int count = 0;
        while (keyIter.hasNext()) {
            System.out.println("  " + keyIter.next());
            count++;
        }
        System.out.println("Total keys: " + count);

        System.out.println("Entries via iterator:");
        Iterator<MapEntry<String, Integer>> entryIter = map.entryIterator();
        while (entryIter.hasNext()) {
            MapEntry<String, Integer> entry = entryIter.next();
            System.out.println("  " + entry.getKey() + " = " + entry.getValue());
        }
        System.out.println();
    }

    private static void testEntrySet() {
        System.out.println("=== Test: EntrySet ===");
        LinearProbingHashMap<Integer, String> map = new LinearProbingHashMap<>(16, false);

        map.put(1, "one");
        map.put(2, "two");
        map.put(3, "three");

        Set<MapEntry<Integer, String>> entries = map.entrySet();
        System.out.println("EntrySet size: " + entries.size());
        for (MapEntry<Integer, String> entry : entries) {
            System.out.println("  " + entry.getKey() + " -> " + entry.getValue());
        }
        System.out.println();
    }

    private static void testFunctionalOperations() {
        System.out.println("=== Test: Functional Operations ===");
        LinearProbingHashMap<String, Integer> map = new LinearProbingHashMap<>(16, false);

        // Test getOrDefault
        map.put("apple", 5);
        System.out.println("getOrDefault('apple', 0): " + map.getOrDefault("apple", 0));
        System.out.println("getOrDefault('missing', 99): " + map.getOrDefault("missing", 99));

        // Test putIfAbsent
        System.out.println("putIfAbsent('orange', 3): " + map.putIfAbsent("orange", 3));
        System.out.println("putIfAbsent('apple', 10): " + map.putIfAbsent("apple", 10));
        System.out.println("apple value after putIfAbsent: " + map.get("apple"));

        // Test computeIfAbsent
        Integer banana = map.computeIfAbsent("banana", k -> 2);
        System.out.println("computeIfAbsent('banana', mapping): " + banana);

        // Test computeIfPresent
        Integer result = map.computeIfPresent("apple", (k, v) -> v * 2);
        System.out.println("computeIfPresent('apple', double): " + result);
        System.out.println("apple after compute: " + map.get("apple"));

        System.out.println();
    }

    private static void testForEach() {
        System.out.println("=== Test: ForEach ===");
        LinearProbingHashMap<String, Integer> scores = new LinearProbingHashMap<>(16, false);

        scores.put("Alice", 95);
        scores.put("Bob", 87);
        scores.put("Charlie", 92);

        System.out.println("Scores:");
        scores.forEach((name, score) ->
            System.out.println("  " + name + ": " + score)
        );
        System.out.println();
    }
}
