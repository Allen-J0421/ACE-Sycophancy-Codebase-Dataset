import java.util.Arrays;

class hashNode {
    final int key;
    final int value;

    public hashNode(int key, int value) {
        this.key = key;
        this.value = value;
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

    hashNode[] arr;
    SlotState[] states;
    int capacity;
    int size;
    final hashNode dummy;

    public hashMap() {
        capacity = DEFAULT_CAPACITY;
        size = 0;
        arr = new hashNode[capacity];
        states = new SlotState[capacity];
        Arrays.fill(states, SlotState.EMPTY);
        dummy = new hashNode(EMPTY_RESULT, EMPTY_RESULT);
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

    private int probe(int key, boolean stopAtEmpty) {
        int hashIndex = indexForKey(key);
        int firstDeleted = EMPTY_RESULT;
        int probes = 0;

        while (probes < capacity) {
            SlotState state = states[hashIndex];
            if (state == SlotState.EMPTY) {
                if (stopAtEmpty) {
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

        return stopAtEmpty ? EMPTY_RESULT : firstDeleted;
    }

    private int findSlotForLookup(int key) {
        return probe(key, true);
    }

    private int findSlotForInsert(int key) {
        return probe(key, false);
    }

    private void rehashToCapacity(int newCapacity) {
        hashNode[] previous = arr;
        SlotState[] previousStates = states;
        capacity = newCapacity;
        arr = new hashNode[capacity];
        states = new SlotState[capacity];
        Arrays.fill(states, SlotState.EMPTY);
        size = 0;

        for (int i = 0; i < previous.length; i++) {
            if (previousStates[i] == SlotState.OCCUPIED) {
                placeNodeWithoutResize(previous[i].key, previous[i].value);
            }
        }
    }

    private void resizeIfNeeded() {
        if (size * 2 < capacity) {
            return;
        }

        rehashToCapacity(capacity * 2);
    }

    private void placeNodeWithoutResize(int key, int value) {
        int index = findSlotForInsert(key);
        if (index == EMPTY_RESULT) {
            return;
        }

        if (!isOccupied(index)) {
            size++;
        }
        arr[index] = new hashNode(key, value);
        states[index] = SlotState.OCCUPIED;
    }

    private void placeNode(int key, int value) {
        resizeIfNeeded();
        placeNodeWithoutResize(key, value);
    }

    void insertNode(int key, int value) {
        placeNode(key, value);
    }

    private int findExistingIndex(int key) {
        return findSlotForLookup(key);
    }

    int deleteNode(int key) {
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

    int get(int key) {
        int hashIndex = findExistingIndex(key);
        if (hashIndex == EMPTY_RESULT) {
            return EMPTY_RESULT;
        }

        return arr[hashIndex].value;
    }

    int sizeofMap() {
        return size;
    }

    boolean isEmpty() {
        return size == 0;
    }

    void display() {
        for (int i = 0; i < capacity; i++) {
            if (isOccupied(i)) {
                System.out.println(arr[i].key +
                " " + arr[i].value);
            }
        }
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
