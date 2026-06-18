final class HashNode {
    final int key;
    final int value;

    HashNode(int key, int value) {
        this.key = key;
        this.value = value;
    }
}

class LinearProbingHashMap {
    private static final int DEFAULT_CAPACITY = 20;
    private static final HashNode DELETED_NODE = new HashNode(0, 0);

    private final HashNode[] table;
    private final int capacity;
    private int size;

    LinearProbingHashMap() {
        capacity = DEFAULT_CAPACITY;
        table = new HashNode[capacity];
    }

    private int indexFor(int key) {
        return Math.floorMod(key, capacity);
    }

    void insertNode(int key, int value) {
        int insertIndex = findInsertIndex(key);
        if (table[insertIndex] == null || table[insertIndex] == DELETED_NODE) {
            size++;
        }
        table[insertIndex] = new HashNode(key, value);
    }

    int deleteNode(int key) {
        int existingIndex = findExistingIndex(key);
        if (existingIndex == -1) {
            return -1;
        }

        int value = table[existingIndex].value;
        table[existingIndex] = DELETED_NODE;
        size--;
        return value;
    }

    int get(int key) {
        int existingIndex = findExistingIndex(key);
        return existingIndex == -1 ? -1 : table[existingIndex].value;
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
                System.out.println(table[i].key + " " + table[i].value);
            }
        }
    }

    private int findExistingIndex(int key) {
        int startIndex = indexFor(key);

        for (int offset = 0; offset < capacity; offset++) {
            int index = (startIndex + offset) % capacity;
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

        for (int offset = 0; offset < capacity; offset++) {
            int index = (startIndex + offset) % capacity;
            if (table[index] == null) {
                return firstDeletedIndex == -1 ? index : firstDeletedIndex;
            }
            if (table[index] == DELETED_NODE && firstDeletedIndex == -1) {
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
        return table[index] != null && table[index] != DELETED_NODE;
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
