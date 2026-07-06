class HashMap<K, V> implements HashMapOperations<K, V> {
    private HashNode<K, V>[] table;
    private int capacity;
    private int size;
    private final HashNode<K, V> DELETED = new HashNode<>(null, null);
    private static final double LOAD_FACTOR_THRESHOLD = 0.75;

    @SuppressWarnings("unchecked")
    public HashMap() {
        capacity = 20;
        size = 0;
        table = (HashNode<K, V>[]) new HashNode[capacity];
    }

    private int hash(K key) {
        return (key.hashCode() & 0x7FFFFFFF) % capacity;
    }

    @Override
    public void insertNode(K key, V value) {
        HashNode<K, V> temp = new HashNode<>(key, value);
        int hashIndex = hash(key);

        while (table[hashIndex] != null &&
               !key.equals(table[hashIndex].getKey()) &&
               table[hashIndex] != DELETED) {
            hashIndex++;
            hashIndex %= capacity;
        }

        if (table[hashIndex] == null || table[hashIndex] == DELETED)
            size++;
        table[hashIndex] = temp;

        if (size > capacity * LOAD_FACTOR_THRESHOLD)
            resize();
    }

    @Override
    public V deleteNode(K key) {
        int hashIndex = hash(key);

        while (table[hashIndex] != null) {
            if (key.equals(table[hashIndex].getKey())) {
                V value = table[hashIndex].getValue();
                table[hashIndex] = DELETED;
                size--;
                return value;
            }
            hashIndex++;
            hashIndex %= capacity;
        }

        return null;
    }

    @Override
    public V get(K key) {
        int hashIndex = hash(key);
        int counter = 0;

        while (table[hashIndex] != null) {
            if (counter++ > capacity)
                return null;

            if (key.equals(table[hashIndex].getKey()))
                return table[hashIndex].getValue();
            hashIndex++;
            hashIndex %= capacity;
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
}
