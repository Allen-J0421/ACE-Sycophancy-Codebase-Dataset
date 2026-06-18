import java.io.PrintStream;

class LinearProbingHashMap {
    private static final int DEFAULT_CAPACITY = 20;
    private static final int NOT_FOUND = -1;
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
        return removeOrDefault(key, NOT_FOUND);
    }

    int removeOrDefault(int key, int defaultValue) {
        int existingIndex = findExistingIndex(key);
        if (existingIndex == NOT_FOUND) {
            return defaultValue;
        }

        int value = table[existingIndex].value;
        table[existingIndex] = DELETED_ENTRY;
        size--;
        return value;
    }

    int get(int key) {
        return getOrDefault(key, NOT_FOUND);
    }

    int getOrDefault(int key, int defaultValue) {
        int existingIndex = findExistingIndex(key);
        return existingIndex == NOT_FOUND ? defaultValue : table[existingIndex].value;
    }

    boolean containsKey(int key) {
        return findExistingIndex(key) != NOT_FOUND;
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
        printEntries(System.out);
    }

    void printEntries(PrintStream output) {
        for (int i = 0; i < table.length; i++) {
            if (isOccupied(i)) {
                output.println(table[i].key + " " + table[i].value);
            }
        }
    }

    private int findExistingIndex(int key) {
        int startIndex = indexFor(key);

        for (int offset = 0; offset < table.length; offset++) {
            int index = probeIndex(startIndex, offset);
            if (table[index] == null) {
                return NOT_FOUND;
            }
            if (isOccupied(index) && table[index].key == key) {
                return index;
            }
        }

        return NOT_FOUND;
    }

    private int findInsertIndex(int key) {
        int startIndex = indexFor(key);
        int firstDeletedIndex = NOT_FOUND;

        for (int offset = 0; offset < table.length; offset++) {
            int index = probeIndex(startIndex, offset);
            if (table[index] == null) {
                return firstDeletedIndex == NOT_FOUND ? index : firstDeletedIndex;
            }
            if (isDeleted(index) && firstDeletedIndex == NOT_FOUND) {
                firstDeletedIndex = index;
            } else if (isOccupied(index) && table[index].key == key) {
                return index;
            }
        }

        if (firstDeletedIndex != NOT_FOUND) {
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
        LinearProbingHashMap map = new LinearProbingHashMap();
        map.put(1, 1);
        map.put(2, 2);
        map.put(2, 3);
        map.printEntries();
        System.out.println(map.size());
        System.out.println(map.remove(2));
        System.out.println(map.size());
        System.out.println(map.isEmpty());
        System.out.println(map.get(2));
    }
}
