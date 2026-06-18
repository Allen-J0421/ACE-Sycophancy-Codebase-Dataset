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

interface IntMap {
    void put(int key, int value);
    int get(int key);
    int remove(int key);
    int size();
    boolean isEmpty();
    boolean containsKey(int key);
    void clear();
}

class LinearProbingHashMap implements IntMap {
    private static final int MIN_CAPACITY = 16;
    private static final int MAX_CAPACITY = 1 << 30;
    private static final float UPPER_LOAD_FACTOR = 0.75f;
    private static final float LOWER_LOAD_FACTOR = 0.25f;
    private static final int NOT_FOUND = -1;

    private HashEntry[] table;
    private int size;
    private int capacity;
    private int deletedCount;

    public LinearProbingHashMap() {
        this(MIN_CAPACITY);
    }

    public LinearProbingHashMap(int initialCapacity) {
        if (initialCapacity < MIN_CAPACITY) {
            initialCapacity = MIN_CAPACITY;
        }
        if (initialCapacity > MAX_CAPACITY) {
            throw new IllegalArgumentException("Initial capacity exceeds maximum");
        }
        if (!isPowerOfTwo(initialCapacity)) {
            initialCapacity = nextPowerOfTwo(initialCapacity);
        }
        this.capacity = initialCapacity;
        this.size = 0;
        this.deletedCount = 0;
        this.table = new HashEntry[capacity];
    }

    @Override
    public void put(int key, int value) {
        if (shouldExpandResize()) {
            resize(capacity * 2);
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

            if (shouldShrinkResize()) {
                resize(capacity / 2);
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

    private int hash(int key) {
        return Math.abs(key) % capacity;
    }

    private int findSlot(int key) {
        int index = hash(key);
        int firstDeleted = -1;

        for (int i = 0; i < capacity; i++) {
            int probeIndex = (index + i) % capacity;
            HashEntry entry = table[probeIndex];

            if (entry == null) {
                return firstDeleted >= 0 ? firstDeleted : probeIndex;
            }
            if (entry.matches(key)) {
                return probeIndex;
            }
            if (entry.deleted && firstDeleted < 0) {
                firstDeleted = probeIndex;
            }
        }
        return -1;
    }

    private int findExisting(int key) {
        int index = hash(key);

        for (int i = 0; i < capacity; i++) {
            int probeIndex = (index + i) % capacity;
            HashEntry entry = table[probeIndex];

            if (entry == null) {
                return -1;
            }
            if (entry.matches(key)) {
                return probeIndex;
            }
        }
        return -1;
    }

    private boolean shouldExpandResize() {
        return (size + deletedCount) >= capacity * UPPER_LOAD_FACTOR;
    }

    private boolean shouldShrinkResize() {
        return capacity > MIN_CAPACITY && size <= capacity * LOWER_LOAD_FACTOR;
    }

    private void resize(int newCapacity) {
        newCapacity = Math.max(newCapacity, MIN_CAPACITY);
        newCapacity = Math.min(newCapacity, MAX_CAPACITY);

        if (newCapacity == capacity) {
            return;
        }

        HashEntry[] oldTable = table;
        capacity = newCapacity;
        table = new HashEntry[capacity];
        size = 0;
        deletedCount = 0;

        for (HashEntry entry : oldTable) {
            if (entry != null && entry.isActive()) {
                put(entry.key, entry.value);
            }
        }
    }

    private static boolean isPowerOfTwo(int n) {
        return n > 0 && (n & (n - 1)) == 0;
    }

    private static int nextPowerOfTwo(int n) {
        int power = 1;
        while (power < n && power < MAX_CAPACITY) {
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
        LinearProbingHashMap map = new LinearProbingHashMap(8);

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
    }
}
