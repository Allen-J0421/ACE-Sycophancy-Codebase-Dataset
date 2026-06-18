import java.io.PrintStream;
import java.util.Objects;

public class LinearProbingHashMap {
    private static final int DEFAULT_CAPACITY = 20;
    private static final int NOT_FOUND = -1;
    private static final Entry DELETED_ENTRY = new Entry(0, 0);

    private final Entry[] table;
    private int size;

    public LinearProbingHashMap() {
        this(DEFAULT_CAPACITY);
    }

    public LinearProbingHashMap(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        table = new Entry[capacity];
    }

    /**
     * @deprecated Use {@link #put(int, int)} instead.
     */
    @Deprecated
    public void insertNode(int key, int value) {
        put(key, value);
    }

    public void put(int key, int value) {
        int insertIndex = findInsertIndex(key);
        if (isAvailable(insertIndex)) {
            size++;
        }
        table[insertIndex] = new Entry(key, value);
    }

    /**
     * @deprecated Use {@link #remove(int)} instead.
     */
    @Deprecated
    public int deleteNode(int key) {
        return remove(key);
    }

    public int remove(int key) {
        return removeOrDefault(key, NOT_FOUND);
    }

    public int removeOrDefault(int key, int defaultValue) {
        int existingIndex = findExistingIndex(key);
        if (existingIndex == NOT_FOUND) {
            return defaultValue;
        }

        int value = entryAt(existingIndex).value;
        table[existingIndex] = DELETED_ENTRY;
        size--;
        return value;
    }

    public int get(int key) {
        return getOrDefault(key, NOT_FOUND);
    }

    public int getOrDefault(int key, int defaultValue) {
        int existingIndex = findExistingIndex(key);
        return existingIndex == NOT_FOUND ? defaultValue : entryAt(existingIndex).value;
    }

    public boolean containsKey(int key) {
        return findExistingIndex(key) != NOT_FOUND;
    }

    /**
     * @deprecated Use {@link #size()} instead.
     */
    @Deprecated
    public int sizeofMap() {
        return size();
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * @deprecated Use {@link #printEntries()} instead.
     */
    @Deprecated
    public void display() {
        printEntries();
    }

    public void printEntries() {
        printEntries(System.out);
    }

    public void printEntries(PrintStream output) {
        PrintStream target = Objects.requireNonNull(output, "output");
        for (int i = 0; i < table.length; i++) {
            if (isOccupied(i)) {
                target.println(entryAt(i));
            }
        }
    }

    private int findExistingIndex(int key) {
        int startIndex = indexFor(key);

        for (int offset = 0; offset < table.length; offset++) {
            int index = probeIndex(startIndex, offset);
            if (entryAt(index) == null) {
                return NOT_FOUND;
            }
            if (hasKeyAt(index, key)) {
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
            if (entryAt(index) == null) {
                return firstDeletedIndex == NOT_FOUND ? index : firstDeletedIndex;
            }
            if (isDeleted(index) && firstDeletedIndex == NOT_FOUND) {
                firstDeletedIndex = index;
            } else if (hasKeyAt(index, key)) {
                return index;
            }
        }

        if (firstDeletedIndex != NOT_FOUND) {
            return firstDeletedIndex;
        }
        throw new IllegalStateException("Hash map is full");
    }

    private boolean isOccupied(int index) {
        return entryAt(index) != null && !isDeleted(index);
    }

    private boolean isAvailable(int index) {
        return entryAt(index) == null || isDeleted(index);
    }

    private boolean isDeleted(int index) {
        return entryAt(index) == DELETED_ENTRY;
    }

    private boolean hasKeyAt(int index, int key) {
        return isOccupied(index) && entryAt(index).hasKey(key);
    }

    private Entry entryAt(int index) {
        return table[index];
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

        boolean hasKey(int candidateKey) {
            return key == candidateKey;
        }

        @Override
        public String toString() {
            return key + " " + value;
        }
    }
}
