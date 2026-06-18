import java.util.Iterator;
import java.util.NoSuchElementException;

class HashNode {
    final int key;
    final int value;

    public HashNode(int key, int value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        return key + " " + value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof HashNode)) {
            return false;
        }
        HashNode node = (HashNode) other;
        return key == node.key && value == node.value;
    }

    @Override
    public int hashCode() {
        return 31 * Integer.hashCode(key) + Integer.hashCode(value);
    }
}

class OpenAddressingHashMap implements Iterable<HashNode> {
    private static final int DEFAULT_CAPACITY = 20;
    private static final int EMPTY_RESULT = -1;
    private static final int GROWTH_FACTOR = 2;
    private static final double MAX_LOAD_FACTOR = 0.5;

    private enum SlotState {
        EMPTY,
        OCCUPIED,
        DELETED
    }

    private enum ProbePurpose {
        LOOKUP,
        INSERT
    }

    private static final class Slot {
        int key;
        int value;
        SlotState state;

        Slot(int key, int value, SlotState state) {
            this.key = key;
            this.value = value;
            this.state = state;
        }
    }

    private Slot[] table;
    private int capacity;
    private int size;

    public OpenAddressingHashMap() {
        this(DEFAULT_CAPACITY);
    }

    public OpenAddressingHashMap(int initialCapacity) {
        resetStorage(Math.max(1, initialCapacity));
    }

    private void resetStorage(int newCapacity) {
        capacity = newCapacity;
        size = 0;
        table = new Slot[capacity];
    }

    private int indexForKey(int key) {
        return Math.floorMod(key, capacity);
    }

    private int nextIndex(int index) {
        return (index + 1) % capacity;
    }

    private boolean isOccupied(int index) {
        return table[index] != null && table[index].state == SlotState.OCCUPIED;
    }

    private int probe(int key, ProbePurpose purpose) {
        int hashIndex = indexForKey(key);
        int firstDeleted = EMPTY_RESULT;
        int probes = 0;

        while (probes < capacity) {
            Slot slot = table[hashIndex];
            SlotState state = slot == null ? SlotState.EMPTY : slot.state;
            if (state == SlotState.EMPTY) {
                if (purpose == ProbePurpose.LOOKUP) {
                    return EMPTY_RESULT;
                }
                return firstDeleted != EMPTY_RESULT ? firstDeleted : hashIndex;
            }
            if (state == SlotState.OCCUPIED && slot.key == key) {
                return hashIndex;
            }
            if (state == SlotState.DELETED && firstDeleted == EMPTY_RESULT) {
                firstDeleted = hashIndex;
            }
            hashIndex = nextIndex(hashIndex);
            probes++;
        }

        return purpose == ProbePurpose.LOOKUP ? EMPTY_RESULT : firstDeleted;
    }

    private int findSlotForLookup(int key) {
        return probe(key, ProbePurpose.LOOKUP);
    }

    private int findSlotForInsert(int key) {
        return probe(key, ProbePurpose.INSERT);
    }

    private void rehashToCapacity(int newCapacity) {
        Slot[] previous = table;
        resetStorage(newCapacity);

        for (Slot slot : previous) {
            if (slot != null && slot.state == SlotState.OCCUPIED) {
                insertWithoutResize(slot.key, slot.value);
            }
        }
    }

    private void resizeIfNeeded() {
        if (size < capacity * MAX_LOAD_FACTOR) {
            return;
        }

        rehashToCapacity(capacity * GROWTH_FACTOR);
    }

    private int insertWithoutResize(int key, int value) {
        int index = findSlotForInsert(key);
        if (index == EMPTY_RESULT) {
            return EMPTY_RESULT;
        }

        int previousValue = EMPTY_RESULT;
        Slot slot = table[index];
        if (slot != null && slot.state == SlotState.OCCUPIED) {
            previousValue = slot.value;
            slot.key = key;
            slot.value = value;
        } else if (slot == null) {
            table[index] = new Slot(key, value, SlotState.OCCUPIED);
            size++;
        } else {
            slot.key = key;
            slot.value = value;
            slot.state = SlotState.OCCUPIED;
            size++;
        }

        return previousValue;
    }

    int put(int key, int value) {
        resizeIfNeeded();
        return insertWithoutResize(key, value);
    }

    void insertNode(int key, int value) {
        put(key, value);
    }

    private int findExistingIndex(int key) {
        return findSlotForLookup(key);
    }

    int remove(int key) {
        int hashIndex = findExistingIndex(key);
        if (hashIndex == EMPTY_RESULT) {
            return EMPTY_RESULT;
        }

        int deletedValue = table[hashIndex].value;
        table[hashIndex].state = SlotState.DELETED;
        size--;
        return deletedValue;
    }

    int deleteNode(int key) {
        return remove(key);
    }

    int get(int key) {
        int hashIndex = findExistingIndex(key);
        if (hashIndex == EMPTY_RESULT) {
            return EMPTY_RESULT;
        }

        return table[hashIndex].value;
    }

    boolean containsKey(int key) {
        return findExistingIndex(key) != EMPTY_RESULT;
    }

    void clear() {
        resetStorage(capacity);
    }

    int sizeofMap() {
        return size;
    }

    int size() {
        return size;
    }

    boolean isEmpty() {
        return size == 0;
    }

    void display() {
        System.out.print(toString());
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (HashNode node : this) {
            builder.append(node).append(System.lineSeparator());
        }
        return builder.toString();
    }

    @Override
    public Iterator<HashNode> iterator() {
        return new EntryIterator();
    }

    private final class EntryIterator implements Iterator<HashNode> {
        private int index = advance(0);

        private int advance(int start) {
            int current = start;
            while (current < capacity && !isOccupied(current)) {
                current++;
            }
            return current;
        }

        @Override
        public boolean hasNext() {
            return index < capacity;
        }

        @Override
        public HashNode next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            Slot slot = table[index];
            HashNode node = new HashNode(slot.key, slot.value);
            index = advance(index + 1);
            return node;
        }
    }
}

class OpenAddressingHashMapDemo {
    public static void main(String[] args) {
        OpenAddressingHashMap h = new OpenAddressingHashMap();
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
