class HashEntry {
    final int key;
    int value;
    boolean deleted;

    HashEntry(int key, int value) {
        this.key = key;
        this.value = value;
        this.deleted = false;
    }

    boolean isActive() {
        return !deleted;
    }

    boolean matches(int k) {
        return key == k && !deleted;
    }

    void markDeleted() {
        this.deleted = true;
    }

    @Override
    public String toString() {
        return key + " -> " + value + (deleted ? " [DELETED]" : "");
    }
}

class HashStats {
    private int totalProbes;
    private int probeCount;
    private int maxProbeDepth;
    private int collisions;
    private int resizeCount;

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

    void reset() {
        totalProbes = 0;
        probeCount = 0;
        maxProbeDepth = 0;
        collisions = 0;
        resizeCount = 0;
    }

    int getTotalProbes() {
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

    void print() {
        System.out.println("  Total Probes: " + totalProbes);
        System.out.println("  Max Probe Depth: " + maxProbeDepth);
        System.out.println("  Collisions: " + collisions);
        System.out.println("  Resize Count: " + resizeCount);
    }
}

interface IntMap {
    void put(int key, int value);
    int get(int key);
    int remove(int key);
    int size();
    boolean isEmpty();
    boolean containsKey(int key);
    void clear();
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

interface HashFunction {
    int hash(int key, int capacity);
}

class ModuloHashFunction implements HashFunction {
    @Override
    public int hash(int key, int capacity) {
        return Math.abs(key) % capacity;
    }

    @Override
    public String toString() {
        return "ModuloHashFunction";
    }
}

class QuadraticHashFunction implements HashFunction {
    private static final int MULTIPLIER = 31;

    @Override
    public int hash(int key, int capacity) {
        int h = Math.abs(key) * MULTIPLIER;
        h = h ^ (h >>> 16);
        return h % capacity;
    }

    @Override
    public String toString() {
        return "QuadraticHashFunction";
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

class LinearProbingHashMap implements IntMap {
    private static final int NOT_FOUND = -1;

    private HashEntry[] table;
    private int size;
    private int capacity;
    private int deletedCount;

    private ResizeStrategy resizeStrategy;
    private HashFunction hashFunction;
    private ProbeSequence probeSequence;
    private HashStats stats;
    private boolean trackStats;

    public LinearProbingHashMap() {
        this(new DefaultResizeStrategy().getMinCapacity(), true);
    }

    public LinearProbingHashMap(int initialCapacity, boolean trackStats) {
        this(initialCapacity, new DefaultResizeStrategy(),
             new ModuloHashFunction(), new LinearProbeSequence(), trackStats);
    }

    public LinearProbingHashMap(int initialCapacity, ResizeStrategy strategy,
                               HashFunction hash, ProbeSequence probe, boolean trackStats) {
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
    public void put(int key, int value) {
        if (key == NOT_FOUND) {
            throw new IllegalArgumentException("Key cannot be " + NOT_FOUND);
        }

        if (resizeStrategy.shouldExpand(size, deletedCount, capacity)) {
            resize(resizeStrategy.nextCapacity(capacity, true));
        }

        int index = findSlot(key);
        if (index < 0) {
            throw new RuntimeException("Hash table full - probe sequence exhausted");
        }

        boolean isNewEntry = table[index] == null || !table[index].isActive();
        if (table[index] != null && table[index].deleted) {
            deletedCount--;
        }

        table[index] = new HashEntry(key, value);
        if (isNewEntry) {
            size++;
        }
    }

    @Override
    public int get(int key) {
        int index = findExisting(key);
        return index >= 0 ? table[index].value : NOT_FOUND;
    }

    @Override
    public int remove(int key) {
        int index = findExisting(key);
        if (index >= 0) {
            int value = table[index].value;
            table[index].markDeleted();
            size--;
            deletedCount++;

            if (resizeStrategy.shouldShrink(size, capacity)) {
                resize(resizeStrategy.nextCapacity(capacity, false));
            }
            return value;
        }
        return NOT_FOUND;
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
    public boolean containsKey(int key) {
        return findExisting(key) >= 0;
    }

    @Override
    public void clear() {
        size = 0;
        deletedCount = 0;
        table = new HashEntry[capacity];
        stats.reset();
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
            HashEntry entry = table[i];
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

    private int findSlot(int key) {
        int startIndex = hashFunction.hash(key, capacity);
        int firstDeleted = -1;

        for (int i = 0; i < capacity; i++) {
            int probeIndex = probeSequence.nextProbe(startIndex, i, capacity);
            if (probeIndex < 0 || probeIndex >= capacity) {
                probeIndex = probeIndex % capacity;
                if (probeIndex < 0) probeIndex += capacity;
            }

            HashEntry entry = table[probeIndex];

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

    private int findExisting(int key) {
        int startIndex = hashFunction.hash(key, capacity);

        for (int i = 0; i < capacity; i++) {
            int probeIndex = probeSequence.nextProbe(startIndex, i, capacity);
            if (probeIndex < 0 || probeIndex >= capacity) {
                probeIndex = probeIndex % capacity;
                if (probeIndex < 0) probeIndex += capacity;
            }

            HashEntry entry = table[probeIndex];

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

        HashEntry[] oldTable = table;
        capacity = newCapacity;
        table = new HashEntry[capacity];
        int oldSize = size;
        size = 0;
        deletedCount = 0;

        if (trackStats) {
            stats.recordResize();
        }

        for (HashEntry entry : oldTable) {
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
    private static final int MAX_POWER_OF_TWO = 1 << 30;

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

class HashMapBuilder {
    private int capacity = 16;
    private ResizeStrategy strategy = new DefaultResizeStrategy();
    private HashFunction hash = new ModuloHashFunction();
    private ProbeSequence probe = new LinearProbeSequence();
    private boolean trackStats = false;

    public HashMapBuilder capacity(int capacity) {
        this.capacity = capacity;
        return this;
    }

    public HashMapBuilder strategy(ResizeStrategy strategy) {
        this.strategy = strategy;
        return this;
    }

    public HashMapBuilder hashFunction(HashFunction hash) {
        this.hash = hash;
        return this;
    }

    public HashMapBuilder probeSequence(ProbeSequence probe) {
        this.probe = probe;
        return this;
    }

    public HashMapBuilder trackStats(boolean trackStats) {
        this.trackStats = trackStats;
        return this;
    }

    public LinearProbingHashMap build() {
        return new LinearProbingHashMap(capacity, strategy, hash, probe, trackStats);
    }
}

class Demo {
    public static void main(String[] args) {
        testBasicOperations();
        testResizing();
        testContainsAndClear();
        testCustomStrategies();
        testStatistics();
        testBuilderPattern();
        testProbeSequences();
        testErrorHandling();
    }

    private static void testBasicOperations() {
        System.out.println("=== Test: Basic Operations ===");
        LinearProbingHashMap map = new LinearProbingHashMap();

        map.put(1, 100);
        map.put(2, 200);
        map.put(2, 300);

        System.out.println("Get(1): " + map.get(1));
        System.out.println("Get(2): " + map.get(2));
        System.out.println("Size: " + map.size());

        int removed = map.remove(2);
        System.out.println("Removed 2 (value was): " + removed);
        System.out.println("Get(2) after removal: " + map.get(2));
        System.out.println("Size: " + map.size());
        System.out.println("Is empty: " + map.isEmpty());
        System.out.println();
    }

    private static void testResizing() {
        System.out.println("=== Test: Dynamic Resizing ===");
        LinearProbingHashMap map = new LinearProbingHashMap(16, false);

        for (int i = 0; i < 15; i++) {
            map.put(i, i * 10);
            if ((i + 1) % 5 == 0) {
                System.out.println("Inserted " + (i + 1) + " items, size: " + map.size());
            }
        }
        System.out.println("Final size: " + map.size());
        System.out.println();
    }

    private static void testContainsAndClear() {
        System.out.println("=== Test: Contains and Clear ===");
        LinearProbingHashMap map = new LinearProbingHashMap();

        map.put(42, 420);
        System.out.println("Contains 42: " + map.containsKey(42));
        System.out.println("Contains 99: " + map.containsKey(99));
        System.out.println("Size before clear: " + map.size());

        map.clear();
        System.out.println("Size after clear: " + map.size());
        System.out.println("Contains 42 after clear: " + map.containsKey(42));
        System.out.println();
    }

    private static void testCustomStrategies() {
        System.out.println("=== Test: Custom Strategies ===");
        ResizeStrategy customStrategy = new DefaultResizeStrategy();
        HashFunction customHash = new ModuloHashFunction();
        ProbeSequence customProbe = new LinearProbeSequence();

        LinearProbingHashMap map = new LinearProbingHashMap(16, customStrategy,
                                                           customHash, customProbe, false);
        for (int i = 0; i < 10; i++) {
            map.put(i * 7, i * 100);
        }
        System.out.println("Inserted 10 items with custom strategies");
        System.out.println("Size: " + map.size());
        System.out.println("Get(21): " + map.get(21));
        System.out.println();
    }

    private static void testStatistics() {
        System.out.println("=== Test: Statistics ===");
        LinearProbingHashMap map = new LinearProbingHashMap(16, true);

        for (int i = 0; i < 12; i++) {
            map.put(i * 11, i * 100);
        }
        System.out.println("After inserting 12 items:");
        map.printStats();
        System.out.println();
    }

    private static void testBuilderPattern() {
        System.out.println("=== Test: Builder Pattern ===");
        LinearProbingHashMap map = new HashMapBuilder()
            .capacity(32)
            .hashFunction(new QuadraticHashFunction())
            .probeSequence(new LinearProbeSequence())
            .trackStats(false)
            .build();

        for (int i = 0; i < 8; i++) {
            map.put(i * 13, i * 50);
        }
        System.out.println("Built map with custom config");
        System.out.println("Size: " + map.size());
        System.out.println("Get(39): " + map.get(39));
        System.out.println();
    }

    private static void testProbeSequences() {
        System.out.println("=== Test: Probe Sequences ===");

        LinearProbingHashMap linearMap = new HashMapBuilder()
            .capacity(16)
            .probeSequence(new LinearProbeSequence())
            .trackStats(true)
            .build();

        for (int i = 0; i < 8; i++) {
            linearMap.put(i * 17, i * 100);
        }
        System.out.println("Linear Probing:");
        linearMap.printStats();

        LinearProbingHashMap quadraticMap = new HashMapBuilder()
            .capacity(16)
            .probeSequence(new QuadraticProbeSequence())
            .trackStats(true)
            .build();

        for (int i = 0; i < 8; i++) {
            quadraticMap.put(i * 17, i * 100);
        }
        System.out.println("Quadratic Probing:");
        quadraticMap.printStats();
        System.out.println();
    }

    private static void testErrorHandling() {
        System.out.println("=== Test: Error Handling ===");
        LinearProbingHashMap map = new LinearProbingHashMap();

        try {
            map.put(-1, 100);
            System.out.println("ERROR: Should have rejected key -1");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Correctly rejected invalid key: " + e.getMessage());
        }

        try {
            new LinearProbingHashMap(5, false);
            System.out.println("ERROR: Should have rejected capacity 5");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Correctly rejected invalid capacity: " + e.getMessage());
        }

        System.out.println();
    }
}
