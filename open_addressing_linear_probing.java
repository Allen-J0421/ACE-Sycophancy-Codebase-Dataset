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
    private static final double MAX_LOAD_FACTOR = 0.5;

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

    private int findExistingIndex(int key) {
        int hashIndex = hashCode(key);
        int probes = 0;

        while (probes < capacity) {
            hashNode current = arr[hashIndex];
            if (current == null) {
                return EMPTY_RESULT;
            }
            if (current.key == key) {
                return hashIndex;
            }
            hashIndex++;
            hashIndex %= capacity;
            probes++;
        }

        return EMPTY_RESULT;
    }

    private int findInsertIndex(int key) {
        int hashIndex = hashCode(key);
        int firstTombstone = EMPTY_RESULT;
        int probes = 0;

        while (probes < capacity) {
            hashNode current = arr[hashIndex];
            if (current == null) {
                return firstTombstone != EMPTY_RESULT ? firstTombstone : hashIndex;
            }
            if (current.key == key) {
                return hashIndex;
            }
            if (current.key == dummy.key && firstTombstone == EMPTY_RESULT) {
                firstTombstone = hashIndex;
            }
            hashIndex++;
            hashIndex %= capacity;
            probes++;
        }

        return firstTombstone;
    }

    private void resizeIfNeeded() {
        if (size < capacity * MAX_LOAD_FACTOR) {
            return;
        }

        hashNode[] previous = arr;
        capacity *= 2;
        arr = new hashNode[capacity];
        size = 0;

        for (hashNode node : previous) {
            if (node != null && node.key != dummy.key) {
                insertNode(node.key, node.value);
            }
        }
    }

    void insertNode(int key, int value) {
        resizeIfNeeded();

        int index = findInsertIndex(key);
        if (index == EMPTY_RESULT) {
            return;
        }

        if (arr[index] == null || arr[index].key == dummy.key) {
            size++;
        }
        arr[index] = new hashNode(key, value);
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
            if (arr[i] != null && arr[i].key != dummy.key) {
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
