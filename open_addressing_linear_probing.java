import java.util.Arrays;

class hashNode {
    final int key;
    final int value;

    public hashNode(int key, int value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        return key + " " + value;
    }
}

class hashMap {
    private static final int DEFAULT_CAPACITY = 20;
    private static final int EMPTY_RESULT = -1;

    private enum SlotState {
        EMPTY,
        OCCUPIED,
        DELETED
    }

    private enum ProbePurpose {
        LOOKUP,
        INSERT
    }

    hashNode[] arr;
    SlotState[] states;
    int capacity;
    int size;
    final hashNode dummy;

    public hashMap() {
        this(DEFAULT_CAPACITY);
    }

    public hashMap(int initialCapacity) {
        resetStorage(Math.max(1, initialCapacity));
        dummy = new hashNode(EMPTY_RESULT, EMPTY_RESULT);
    }

    private void resetStorage(int newCapacity) {
        capacity = newCapacity;
        size = 0;
        arr = new hashNode[capacity];
        states = new SlotState[capacity];
        Arrays.fill(states, SlotState.EMPTY);
    }

    private int indexForKey(int key) {
        return Math.floorMod(key, capacity);
    }

    private int nextIndex(int index) {
        return (index + 1) % capacity;
    }

    private boolean isOccupied(int index) {
        return states[index] == SlotState.OCCUPIED;
    }

    private boolean isDeleted(int index) {
        return states[index] == SlotState.DELETED;
    }

    private int probe(int key, ProbePurpose purpose) {
        int hashIndex = indexForKey(key);
        int firstDeleted = EMPTY_RESULT;
        int probes = 0;

        while (probes < capacity) {
            SlotState state = states[hashIndex];
            if (state == SlotState.EMPTY) {
                if (purpose == ProbePurpose.LOOKUP) {
                    return EMPTY_RESULT;
                }
                return firstDeleted != EMPTY_RESULT ? firstDeleted : hashIndex;
            }
            if (state == SlotState.OCCUPIED && arr[hashIndex].key == key) {
                return hashIndex;
            }
            if (isDeleted(hashIndex) && firstDeleted == EMPTY_RESULT) {
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
        hashNode[] previous = arr;
        SlotState[] previousStates = states;
        resetStorage(newCapacity);

        for (int i = 0; i < previous.length; i++) {
            if (previousStates[i] == SlotState.OCCUPIED) {
                insertWithoutResize(previous[i].key, previous[i].value);
            }
        }
    }

    private void resizeIfNeeded() {
        if (size * 2 < capacity) {
            return;
        }

        rehashToCapacity(capacity * 2);
    }

    private int insertWithoutResize(int key, int value) {
        int index = findSlotForInsert(key);
        if (index == EMPTY_RESULT) {
            return EMPTY_RESULT;
        }

        int previousValue = EMPTY_RESULT;
        if (isOccupied(index)) {
            previousValue = arr[index].value;
        } else {
            size++;
        }

        arr[index] = new hashNode(key, value);
        states[index] = SlotState.OCCUPIED;
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

        int deletedValue = arr[hashIndex].value;
        arr[hashIndex] = null;
        states[hashIndex] = SlotState.DELETED;
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

        return arr[hashIndex].value;
    }

    boolean containsKey(int key) {
        return findExistingIndex(key) != EMPTY_RESULT;
    }

    void clear() {
        Arrays.fill(arr, null);
        Arrays.fill(states, SlotState.EMPTY);
        size = 0;
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

    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < capacity; i++) {
            if (isOccupied(i)) {
                builder.append(arr[i]).append(System.lineSeparator());
            }
        }
        return builder.toString();
    }

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
