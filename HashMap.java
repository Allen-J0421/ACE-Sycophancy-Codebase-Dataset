class HashMap<K, V> implements HashMapOperations<K, V> {
    private HashNode<K, V>[] table;
    private int capacity;
    private int size;
    private final int initialCapacity;
    private final HashNode<K, V> DELETED = new HashNode<>(null, null);
    private static final double LOAD_FACTOR_THRESHOLD = 0.75;
    private static final int DEFAULT_CAPACITY = 20;
    private final ProbingStrategy strategy;

    public HashMap() {
        this(DEFAULT_CAPACITY, ProbingStrategies.linear());
    }

    public HashMap(ProbingStrategy strategy) {
        this(DEFAULT_CAPACITY, strategy);
    }

    public HashMap(int initialCapacity) {
        this(initialCapacity, ProbingStrategies.linear());
    }

    @SuppressWarnings("unchecked")
    public HashMap(int initialCapacity, ProbingStrategy strategy) {
        this.strategy = strategy;
        this.initialCapacity = initialCapacity;
        this.capacity = initialCapacity;
        this.size = 0;
        this.table = (HashNode<K, V>[]) new HashNode[capacity];
    }

    private int hash1(K key) {
        return (key.hashCode() & 0x7FFFFFFF) % capacity;
    }

    // Secondary hash for double hashing; guaranteed >= 1 so the step is never zero.
    private int hash2(K key) {
        return 1 + (key.hashCode() & 0x7FFFFFFF) % (capacity - 1);
    }

    @Override
    public void insertNode(K key, V value) {
        int h1 = hash1(key);
        int h2 = hash2(key);
        int attempt = 0;
        int hashIndex = strategy.probe(h1, h2, attempt, capacity);

        while (table[hashIndex] != null &&
               !key.equals(table[hashIndex].getKey()) &&
               table[hashIndex] != DELETED) {
            hashIndex = strategy.probe(h1, h2, ++attempt, capacity);
        }

        if (table[hashIndex] == null || table[hashIndex] == DELETED)
            size++;
        table[hashIndex] = new HashNode<>(key, value);

        if (size > capacity * LOAD_FACTOR_THRESHOLD)
            resize();
    }

    @Override
    public V deleteNode(K key) {
        int h1 = hash1(key);
        int h2 = hash2(key);
        int attempt = 0;
        int hashIndex = strategy.probe(h1, h2, attempt, capacity);

        while (table[hashIndex] != null) {
            if (key.equals(table[hashIndex].getKey())) {
                V value = table[hashIndex].getValue();
                table[hashIndex] = DELETED;
                size--;
                return value;
            }
            hashIndex = strategy.probe(h1, h2, ++attempt, capacity);
        }

        return null;
    }

    @Override
    public V get(K key) {
        int h1 = hash1(key);
        int h2 = hash2(key);
        int attempt = 0;
        int hashIndex = strategy.probe(h1, h2, attempt, capacity);

        while (table[hashIndex] != null && attempt <= capacity) {
            if (key.equals(table[hashIndex].getKey()))
                return table[hashIndex].getValue();
            hashIndex = strategy.probe(h1, h2, ++attempt, capacity);
        }

        return null;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public void display() {
        for (int i = 0; i < capacity; i++) {
            if (table[i] != null && table[i] != DELETED) {
                System.out.println(table[i].getKey() + " " + table[i].getValue());
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void clear() {
        capacity = initialCapacity;
        size = 0;
        table = (HashNode<K, V>[]) new HashNode[capacity];
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        int oldCapacity = capacity;
        HashNode<K, V>[] oldTable = table;

        capacity *= 2;
        size = 0;
        table = (HashNode<K, V>[]) new HashNode[capacity];

        for (int i = 0; i < oldCapacity; i++) {
            if (oldTable[i] != null && oldTable[i] != DELETED)
                insertNode(oldTable[i].getKey(), oldTable[i].getValue());
        }
    }

    private static class HashNode<K, V> {
        private K key;
        private V value;

        HashNode(K key, V value) {
            this.key = key;
            this.value = value;
        }

        K getKey() {
            return key;
        }

        V getValue() {
            return value;
        }
    }
}
