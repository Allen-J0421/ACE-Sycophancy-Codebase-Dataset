class LinearProbingHashMap {
    private static final int DEFAULT_CAPACITY = 20;

    private static class HashNode {
        final int key;
        final int value;

        HashNode(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }

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

    // Returns the table index of a live entry with the given key, or -1 if absent.
    private int indexOf(int key) {
        int index = hash(key);
        for (int probes = 0; table[index] != null && probes < capacity; probes++) {
            if (table[index] != tombstone && table[index].key == key)
                return index;
            index = (index + 1) % capacity;
        }
        return -1;
    }

    // Returns the previous value mapped to the key, or -1 if the key was not present.
    public int put(int key, int value) {
        if (size >= capacity)
            return -1;

        int index = hash(key);
        int insertAt = -1;

        for (int probes = 0; table[index] != null && probes < capacity; probes++) {
            if (table[index] == tombstone) {
                if (insertAt == -1) insertAt = index;
            } else if (table[index].key == key) {
                int previous = table[index].value;
                table[index] = new HashNode(key, value);
                return previous;
            }
            index = (index + 1) % capacity;
        }

        if (insertAt == -1) insertAt = index;
        table[insertAt] = new HashNode(key, value);
        size++;
        return -1;
    }

    public int remove(int key) {
        int index = indexOf(key);
        if (index == -1)
            return -1;

        int value = table[index].value;
        table[index] = tombstone;
        size--;
        return value;
    }

    public int get(int key) {
        int index = indexOf(key);
        return index == -1 ? -1 : table[index].value;
    }

    public boolean containsKey(int key) {
        return indexOf(key) != -1;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < capacity; i++) {
            if (table[i] != null && table[i] != tombstone)
                sb.append(table[i].key).append(" -> ").append(table[i].value).append("\n");
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        LinearProbingHashMap map = new LinearProbingHashMap();
        map.put(1, 1);
        map.put(2, 2);
        System.out.println(map.put(2, 3)); // 2: previous value for key 2
        System.out.print(map);
        System.out.println(map.size());
        System.out.println(map.remove(2));
        System.out.println(map.size());
        System.out.println(map.isEmpty());
        System.out.println(map.get(2));
    }
}
