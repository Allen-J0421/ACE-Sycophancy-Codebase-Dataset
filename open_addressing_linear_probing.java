import java.util.StringJoiner;

interface IntMap {
    /** Returns the previous value mapped to the key, or null if absent. */
    Integer put(int key, int value);
    /** Returns null if the key is not present. */
    Integer get(int key);
    /** Returns null if the key is not present. */
    Integer remove(int key);
    boolean containsKey(int key);
    int size();
    boolean isEmpty();
}

class LinearProbingHashMap implements IntMap {
    private static final int DEFAULT_CAPACITY = 20;

    private record HashNode(int key, int value) {}

    private static final HashNode TOMBSTONE = new HashNode(0, 0);

    private final HashNode[] table;
    private int size;

    public LinearProbingHashMap() {
        this(DEFAULT_CAPACITY);
    }

    public LinearProbingHashMap(int capacity) {
        if (capacity <= 0)
            throw new IllegalArgumentException("Capacity must be positive: " + capacity);
        this.table = new HashNode[capacity];
    }

    private int hash(int key) {
        return (key % table.length + table.length) % table.length;
    }

    private int nextIndex(int index) {
        return (index + 1) % table.length;
    }

    // Returns the table index of a live entry with the given key, or -1 if absent.
    private int indexOf(int key) {
        int index = hash(key);
        for (int probes = 0; table[index] != null && probes < table.length; probes++) {
            if (table[index] != TOMBSTONE && table[index].key() == key)
                return index;
            index = nextIndex(index);
        }
        return -1;
    }

    @Override
    public Integer put(int key, int value) {
        if (size >= table.length)
            throw new IllegalStateException("Hash table is full");

        int index = hash(key);
        int insertAt = -1;

        for (int probes = 0; table[index] != null && probes < table.length; probes++) {
            if (table[index] == TOMBSTONE) {
                if (insertAt == -1) insertAt = index;
            } else if (table[index].key() == key) {
                int previous = table[index].value();
                table[index] = new HashNode(key, value);
                return previous;
            }
            index = nextIndex(index);
        }

        if (insertAt == -1) insertAt = index;
        table[insertAt] = new HashNode(key, value);
        size++;
        return null;
    }

    @Override
    public Integer remove(int key) {
        int index = indexOf(key);
        if (index == -1)
            return null;

        int value = table[index].value();
        table[index] = TOMBSTONE;
        size--;
        return value;
    }

    @Override
    public Integer get(int key) {
        int index = indexOf(key);
        return index == -1 ? null : table[index].value();
    }

    @Override
    public boolean containsKey(int key) {
        return indexOf(key) != -1;
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
    public String toString() {
        StringJoiner sj = new StringJoiner(", ", "{", "}");
        for (HashNode node : table)
            if (node != null && node != TOMBSTONE)
                sj.add(node.key() + " -> " + node.value());
        return sj.toString();
    }
}

class Demo {
    public static void main(String[] args) {
        IntMap map = new LinearProbingHashMap();
        map.put(1, 1);
        map.put(2, 2);
        System.out.println(map.put(2, 3)); // 2: previous value for key 2
        System.out.println(map);
        System.out.println(map.size());
        System.out.println(map.remove(2));
        System.out.println(map.size());
        System.out.println(map.isEmpty());
        System.out.println(map.get(2));    // null: key absent
    }
}
