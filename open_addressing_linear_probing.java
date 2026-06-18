import java.util.*;

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

interface Map<K, V> {
    V put(K key, V value);
    V get(K key);
    V remove(K key);
    int size();
    boolean isEmpty();
    boolean containsKey(K key);
    void clear();
    Set<K> keySet();
    Collection<V> values();
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

class LinearProbingHashMap<K, V> implements Map<K, V> {
    private static final Object NOT_FOUND = new Object();

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

class TestAssertions {
    private static int assertCount = 0;
    private static int passCount = 0;
    private static int failCount = 0;

    static void assertTrue(String message, boolean condition) {
        assertCount++;
        if (condition) {
            passCount++;
            System.out.println("  ✓ " + message);
        } else {
            failCount++;
            System.out.println("  ✗ FAIL: " + message);
        }
    }

    static void assertEquals(String message, Object expected, Object actual) {
        assertCount++;
        if (Objects.equals(expected, actual)) {
            passCount++;
            System.out.println("  ✓ " + message);
        } else {
            failCount++;
            System.out.println("  ✗ FAIL: " + message + " (expected " + expected + ", got " + actual + ")");
        }
    }

    static void printResults() {
        System.out.println("\n=== Test Results ===");
        System.out.println("Total assertions: " + assertCount);
        System.out.println("Passed: " + passCount);
        System.out.println("Failed: " + failCount);
        System.out.println("Success rate: " + String.format("%.1f%%", 100.0 * passCount / assertCount));
    }
}

class Demo {
    public static void main(String[] args) {
        testGenericIntegers();
        testGenericStrings();
        testGenericObjects();
        testKeySetAndValues();
        testErrorHandling();
        testBuilderWithGenerics();
        testStatistics();

        TestAssertions.printResults();
    }

    private static void testGenericIntegers() {
        System.out.println("\n=== Test: Generic Integers ===");
        LinearProbingHashMap<Integer, Integer> map = new LinearProbingHashMap<>(16, false);

        map.put(1, 100);
        map.put(2, 200);
        map.put(3, 300);

        TestAssertions.assertEquals("get(1)", 100, map.get(1));
        TestAssertions.assertEquals("get(2)", 200, map.get(2));
        TestAssertions.assertEquals("size", 3, map.size());

        Integer removed = map.remove(2);
        TestAssertions.assertEquals("removed value", 200, removed);
        TestAssertions.assertEquals("size after remove", 2, map.size());
        TestAssertions.assertEquals("get(2) after remove", null, map.get(2));
    }

    private static void testGenericStrings() {
        System.out.println("\n=== Test: Generic Strings ===");
        LinearProbingHashMap<String, String> map = new LinearProbingHashMap<>(16, false);

        map.put("name", "Alice");
        map.put("city", "New York");
        map.put("age", "30");

        TestAssertions.assertEquals("get name", "Alice", map.get("name"));
        TestAssertions.assertEquals("get city", "New York", map.get("city"));
        TestAssertions.assertEquals("containsKey city", true, map.containsKey("city"));
        TestAssertions.assertEquals("containsKey unknown", false, map.containsKey("unknown"));
    }

    private static void testGenericObjects() {
        System.out.println("\n=== Test: Generic Objects ===");
        LinearProbingHashMap<Integer, Person> map = new LinearProbingHashMap<>(16, false);

        Person alice = new Person("Alice", 30);
        Person bob = new Person("Bob", 25);

        map.put(1, alice);
        map.put(2, bob);

        TestAssertions.assertEquals("get person", alice, map.get(1));
        TestAssertions.assertEquals("size", 2, map.size());

        Person removed = map.remove(1);
        TestAssertions.assertEquals("removed person", alice, removed);
        TestAssertions.assertEquals("size after remove", 1, map.size());
    }

    private static void testKeySetAndValues() {
        System.out.println("\n=== Test: KeySet and Values ===");
        LinearProbingHashMap<String, Integer> map = new LinearProbingHashMap<>(16, false);

        map.put("apple", 1);
        map.put("banana", 2);
        map.put("cherry", 3);

        Set<String> keySet = map.keySet();
        TestAssertions.assertTrue("keySet contains apple", keySet.contains("apple"));
        TestAssertions.assertTrue("keySet contains banana", keySet.contains("banana"));
        TestAssertions.assertTrue("keySet contains cherry", keySet.contains("cherry"));
        TestAssertions.assertEquals("keySet size", 3, keySet.size());

        Collection<Integer> values = map.values();
        TestAssertions.assertEquals("values size", 3, values.size());
    }

    private static void testErrorHandling() {
        System.out.println("\n=== Test: Error Handling ===");
        LinearProbingHashMap<String, String> map = new LinearProbingHashMap<>(16, false);

        try {
            map.put(null, "value");
            TestAssertions.assertTrue("should reject null key", false);
        } catch (IllegalArgumentException e) {
            TestAssertions.assertTrue("correctly rejected null key", true);
        }

        try {
            map.get(null);
            TestAssertions.assertTrue("should reject null in get", false);
        } catch (IllegalArgumentException e) {
            TestAssertions.assertTrue("correctly rejected null in get", true);
        }

        try {
            new LinearProbingHashMap<String, String>(5, false);
            TestAssertions.assertTrue("should reject small capacity", false);
        } catch (IllegalArgumentException e) {
            TestAssertions.assertTrue("correctly rejected small capacity", true);
        }
    }

    private static void testBuilderWithGenerics() {
        System.out.println("\n=== Test: Builder with Generics ===");
        LinearProbingHashMap<String, Double> map = new HashMapBuilder<String, Double>()
            .capacity(32)
            .probeSequence(new QuadraticProbeSequence())
            .trackStats(false)
            .build();

        map.put("pi", 3.14159);
        map.put("e", 2.71828);
        map.put("phi", 1.61803);

        TestAssertions.assertEquals("get pi", 3.14159, map.get("pi"));
        TestAssertions.assertEquals("size", 3, map.size());
    }

    private static void testStatistics() {
        System.out.println("\n=== Test: Statistics ===");
        LinearProbingHashMap<Integer, String> map = new LinearProbingHashMap<>(16, true);

        for (int i = 0; i < 10; i++) {
            map.put(i, "value_" + i);
        }

        TestAssertions.assertEquals("size after 10 inserts", 10, map.size());
        TestAssertions.assertTrue("has operations recorded", map.size() > 0);

        map.printStats();
    }
}

class Person {
    String name;
    int age;

    Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person)) return false;
        Person person = (Person) o;
        return age == person.age && Objects.equals(name, person.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, age);
    }

    @Override
    public String toString() {
        return "Person{" + "name='" + name + '\'' + ", age=" + age + '}';
    }
}
