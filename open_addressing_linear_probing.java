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

    hashNode[] arr;
    int capacity;
    int size;
    final hashNode dummy;

    public hashMap() {
        capacity = DEFAULT_CAPACITY;
        size = 0;
        arr = new hashNode[capacity];
        dummy = new hashNode(EMPTY_RESULT, EMPTY_RESULT);
    }

    private int hashCode(int key) {
        return Math.floorMod(key, capacity);
    }

    private int nextIndex(int index) {
        return (index + 1) % capacity;
    }

    private boolean isTombstone(hashNode node) {
        return node == dummy;
    }

    private boolean isOccupied(hashNode node) {
        return node != null && !isTombstone(node);
    }

    private int probe(int key, boolean stopAtEmpty) {
        int hashIndex = hashCode(key);
        int firstTombstone = EMPTY_RESULT;
        int probes = 0;

        while (probes < capacity) {
            hashNode current = arr[hashIndex];
            if (current == null) {
                if (stopAtEmpty) {
                    return EMPTY_RESULT;
                }
                return firstTombstone != EMPTY_RESULT ? firstTombstone : hashIndex;
            }
            if (current.key == key) {
                return hashIndex;
            }
            if (isTombstone(current) && firstTombstone == EMPTY_RESULT) {
                firstTombstone = hashIndex;
            }
            hashIndex = nextIndex(hashIndex);
            probes++;
        }

        return stopAtEmpty ? EMPTY_RESULT : firstTombstone;
    }

    private int findSlotForLookup(int key) {
        return probe(key, true);
    }

    private int findSlotForInsert(int key) {
        return probe(key, false);
    }

    private void rehashToCapacity(int newCapacity) {
        hashNode[] previous = arr;
        capacity = newCapacity;
        arr = new hashNode[capacity];
        size = 0;

        for (hashNode node : previous) {
            if (isOccupied(node)) {
                placeNodeWithoutResize(node.key, node.value);
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

        if (arr[index] == null || arr[index].key == dummy.key) {
            size++;
        }
        arr[index] = new hashNode(key, value);
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
        arr[hashIndex] = dummy;
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
            if (isOccupied(arr[i])) {
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
