class LinearProbingHashMap {
    private static final int DEFAULT_CAPACITY = 20;
    private static final Entry DELETED_ENTRY = new Entry(0, 0);

    private final Entry[] table;
    private int size;

    LinearProbingHashMap() {
        this(DEFAULT_CAPACITY);
    }

    LinearProbingHashMap(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        table = new Entry[capacity];
    }

    void insertNode(int key, int value) {
        put(key, value);
    }

    void put(int key, int value) {
        int insertIndex = findInsertIndex(key);
        if (isAvailable(insertIndex)) {
            size++;
        }
        table[insertIndex] = new Entry(key, value);
    }

    int deleteNode(int key) {
        return remove(key);
    }

    int remove(int key) {
        int existingIndex = findExistingIndex(key);
        if (existingIndex == -1) {
            return -1;
        }

        int value = table[existingIndex].value;
        table[existingIndex] = DELETED_ENTRY;
        size--;
        return value;
    }

    int get(int key) {
        int existingIndex = findExistingIndex(key);
        return existingIndex == -1 ? -1 : table[existingIndex].value;
    }

    int sizeofMap() {
        return size();
    }

    int size() {
        return size;
    }

    boolean isEmpty() {
        return size == 0;
    }

    void display() {
        printEntries();
    }

    void printEntries() {
        for (int i = 0; i < table.length; i++) {
            if (isOccupied(i)) {
                System.out.println(table[i].key + " " + table[i].value);
            }
        }
    }

    private int findExistingIndex(int key) {
        int startIndex = indexFor(key);

        for (int offset = 0; offset < table.length; offset++) {
            int index = probeIndex(startIndex, offset);
            if (table[index] == null) {
                return -1;
            }
            if (isOccupied(index) && table[index].key == key) {
                return index;
            }
        }

        return -1;
    }

    private int findInsertIndex(int key) {
        int startIndex = indexFor(key);
        int firstDeletedIndex = -1;

        for (int offset = 0; offset < table.length; offset++) {
            int index = probeIndex(startIndex, offset);
            if (table[index] == null) {
                return firstDeletedIndex == -1 ? index : firstDeletedIndex;
            }
            if (isDeleted(index) && firstDeletedIndex == -1) {
                firstDeletedIndex = index;
            } else if (isOccupied(index) && table[index].key == key) {
                return index;
            }
        }

        if (firstDeletedIndex != -1) {
            return firstDeletedIndex;
        }
        throw new IllegalStateException("Hash map is full");
    }

    private boolean isOccupied(int index) {
        return table[index] != null && !isDeleted(index);
    }

    private boolean isAvailable(int index) {
        return table[index] == null || isDeleted(index);
    }

    private boolean isDeleted(int index) {
        return table[index] == DELETED_ENTRY;
    }

    private int indexFor(int key) {
        return Math.floorMod(key, table.length);
    }

    private int probeIndex(int startIndex, int offset) {
        return (startIndex + offset) % table.length;
    }

    private static final class Entry {
        final int key;
        final int value;

        Entry(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }
}

class hashMap extends LinearProbingHashMap {
    public static void main(String[] args) {
        hashMap h = new hashMap();
        h.insertNode(1, 1);
        h.insertNode(2, 2);
        h.insertNode(2, 3);
        h.display();
        System.out.println(h.sizeofMap());
        System.out.println(h.deleteNode(2));
        System.out.println(h.sizeofMap());
        System.out.println(h.isEmpty());
        System.out.println(h.get(2));
    }
}
