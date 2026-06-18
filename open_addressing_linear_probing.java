class HashNode {
    int key;
    int value;

    HashNode(int key, int value) {
        this.key = key;
        this.value = value;
    }
}

class LinearProbingHashMap {
    private static final int DEFAULT_CAPACITY = 20;

    private final HashNode[] table;
    private final int capacity;
    private final HashNode tombstone;
    private int size;

    public LinearProbingHashMap() {
        this(DEFAULT_CAPACITY);
    }

    public LinearProbingHashMap(int capacity) {
        this.capacity = capacity;
        this.table = new HashNode[capacity];
        this.tombstone = new HashNode(0, 0);
    }

    private int hash(int key) {
        return (key % capacity + capacity) % capacity;
    }

    public void put(int key, int value) {
        if (size >= capacity)
            return;

        int index = hash(key);
        while (table[index] != null && table[index] != tombstone && table[index].key != key) {
            index = (index + 1) % capacity;
        }

        if (table[index] == null || table[index] == tombstone)
            size++;
        table[index] = new HashNode(key, value);
    }

    public int remove(int key) {
        int index = hash(key);

        while (table[index] != null) {
            if (table[index] != tombstone && table[index].key == key) {
                int value = table[index].value;
                table[index] = tombstone;
                size--;
                return value;
            }
            index = (index + 1) % capacity;
        }

        return -1;
    }

    public int get(int key) {
        int index = hash(key);

        for (int probes = 0; table[index] != null && probes < capacity; probes++) {
            if (table[index] != tombstone && table[index].key == key)
                return table[index].value;
            index = (index + 1) % capacity;
        }

        return -1;
    }

    public boolean containsKey(int key) {
        return get(key) != -1;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void display() {
        for (int i = 0; i < capacity; i++) {
            if (table[i] != null && table[i] != tombstone)
                System.out.println(table[i].key + " " + table[i].value);
        }
    }

    public static void main(String[] args) {
        LinearProbingHashMap map = new LinearProbingHashMap();
        map.put(1, 1);
        map.put(2, 2);
        map.put(2, 3);
        map.display();
        System.out.println(map.size());
        System.out.println(map.remove(2));
        System.out.println(map.size());
        System.out.println(map.isEmpty());
        System.out.println(map.get(2));
    }
}
