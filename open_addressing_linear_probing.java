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
}

class HashStats {
    int probeDepth;
    int maxProbeDepth;
    int collisions;

    void recordProbe(int depth) {
        probeDepth++;
        maxProbeDepth = Math.max(maxProbeDepth, depth);
    }

    void recordCollision() {
        collisions++;
    }

    void reset() {
        probeDepth = 0;
        maxProbeDepth = 0;
        collisions = 0;
    }

    void print() {
        System.out.println("  Avg Probe Depth: " + (probeDepth > 0 ? probeDepth : 0));
        System.out.println("  Max Probe Depth: " + maxProbeDepth);
        System.out.println("  Total Collisions: " + collisions);
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

    public static int getMinCapacity() {
        return MIN_CAPACITY;
    }

    public static int getMaxCapacity() {
        return MAX_CAPACITY;
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
}

interface ProbeSequence {
    int nextProbe(int startIndex, int probeCount, int capacity);
}

class LinearProbeSequence implements ProbeSequence {
    @Override
    public int nextProbe(int startIndex, int probeCount, int capacity) {
        return (startIndex + probeCount) % capacity;
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
        this(DefaultResizeStrategy.getMinCapacity(), true);
    }

    public LinearProbingHashMap(int initialCapacity, boolean trackStats) {
        this(initialCapacity, new DefaultResizeStrategy(),
             new ModuloHashFunction(), new LinearProbeSequence(), trackStats);
    }

    public LinearProbingHashMap(int initialCapacity, ResizeStrategy strategy,
                               HashFunction hash, ProbeSequence probe, boolean trackStats) {
        validateInitialCapacity(initialCapacity);
        this.capacity = normalizeCapacity(initialCapacity);
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
            System.out.println("  Load Factor: " + String.format("%.2f", (float) size / capacity));
            stats.print();
        }
    }

    public void display() {
        System.out.println("=== Hash Table ===");
        for (int i = 0; i < capacity; i++) {
            HashEntry entry = table[i];
            if (entry == null) {
                System.out.println("[" + i + "] EMPTY");
            } else if (entry.isActive()) {
                System.out.println("[" + i + "] " + entry.key + " -> " + entry.value);
            } else {
                System.out.println("[" + i + "] DELETED");
            }
        }
        System.out.println("Size: " + size + ", Capacity: " + capacity +
                          ", Deleted: " + deletedCount);
    }

    private int findSlot(int key) {
        int startIndex = hashFunction.hash(key, capacity);
        int firstDeleted = -1;

        for (int i = 0; i < capacity; i++) {
            int probeIndex = probeSequence.nextProbe(startIndex, i, capacity);
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
        size = 0;
        deletedCount = 0;
        stats.reset();

        for (HashEntry entry : oldTable) {
            if (entry != null && entry.isActive()) {
                put(entry.key, entry.value);
            }
        }
    }

    private void validateInitialCapacity(int capacity) {
        if (capacity < DefaultResizeStrategy.getMinCapacity()) {
            throw new IllegalArgumentException("Capacity below minimum: " +
                DefaultResizeStrategy.getMinCapacity());
        }
        if (capacity > DefaultResizeStrategy.getMaxCapacity()) {
            throw new IllegalArgumentException("Capacity exceeds maximum: " +
                DefaultResizeStrategy.getMaxCapacity());
        }
    }

    private int normalizeCapacity(int capacity) {
        if (PowerOfTwo.isPowerOfTwo(capacity)) {
            return capacity;
        }
        return PowerOfTwo.nextPowerOfTwo(capacity,
            DefaultResizeStrategy.getMaxCapacity());
    }
}

class PowerOfTwo {
    private static final int MAX_POWER_OF_TWO = 1 << 30;

    static boolean isPowerOfTwo(int n) {
        return n > 0 && (n & (n - 1)) == 0;
    }

    static int nextPowerOfTwo(int n, int max) {
        int power = 1;
        while (power < n && power < max) {
            power <<= 1;
        }
        return power;
    }
}

class Demo {
    public static void main(String[] args) {
        testBasicOperations();
        testResizing();
        testContainsAndClear();
        testCustomStrategies();
        testStatistics();
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
}
