class hashNode {
    final int key;
    final int value;
    final boolean deleted;

    hashNode(int key, int value) {
        this(key, value, false);
    }

    private hashNode(int key, int value, boolean deleted) {
        this.key = key;
        this.value = value;
        this.deleted = deleted;
    }

    static hashNode deletedNode() {
        return new hashNode(0, 0, true);
    }
}

class hashMap {
    private static final int DEFAULT_CAPACITY = 20;
    private static final double MAX_LOAD_FACTOR = 0.6;

    private hashNode[] arr;
    private int capacity;
    private int size;
    private final hashNode dummy;

    hashMap() {
        this(DEFAULT_CAPACITY);
    }

    hashMap(int initialCapacity) {
        capacity = Math.max(1, initialCapacity);
        arr = new hashNode[capacity];
        dummy = hashNode.deletedNode();
    }

    int hashCode(int key) {
        return Math.floorMod(key, capacity);
    }

    void insertNode(int key, int value) {
        ensureCapacity(size + 1);

        int index = findSlotForInsert(key);
        if (index == -1) {
            resize(capacity * 2);
            index = findSlotForInsert(key);
        }

        if (arr[index] == null || arr[index].deleted) {
            size++;
        }
        arr[index] = new hashNode(key, value);
    }

    int deleteNode(int key) {
        int index = findExistingIndex(key);
        if (index == -1) {
            return -1;
        }

        int value = arr[index].value;
        arr[index] = dummy;
        size--;
        return value;
    }

    int get(int key) {
        int index = findExistingIndex(key);
        return index == -1 ? -1 : arr[index].value;
    }

    int sizeofMap() {
        return size;
    }

    boolean isEmpty() {
        return size == 0;
    }

    void display() {
        for (hashNode node : arr) {
            if (node != null && !node.deleted) {
                System.out.println(node.key + " " + node.value);
            }
        }
    }

    private void ensureCapacity(int targetSize) {
        if ((double) targetSize / capacity > MAX_LOAD_FACTOR) {
            resize(capacity * 2);
        }
    }

    private int findExistingIndex(int key) {
        int hashIndex = hashCode(key);

        for (int probes = 0; probes < capacity; probes++) {
            hashNode node = arr[hashIndex];
            if (node == null) {
                return -1;
            }
            if (!node.deleted && node.key == key) {
                return hashIndex;
            }
            hashIndex = nextIndex(hashIndex);
        }

        return -1;
    }

    private int findSlotForInsert(int key) {
        int hashIndex = hashCode(key);
        int firstDeletedIndex = -1;

        for (int probes = 0; probes < capacity; probes++) {
            hashNode node = arr[hashIndex];

            if (node == null) {
                return firstDeletedIndex != -1 ? firstDeletedIndex : hashIndex;
            }
            if (node.deleted) {
                if (firstDeletedIndex == -1) {
                    firstDeletedIndex = hashIndex;
                }
            } else if (node.key == key) {
                return hashIndex;
            }

            hashIndex = nextIndex(hashIndex);
        }

        return firstDeletedIndex;
    }

    private int nextIndex(int index) {
        return (index + 1) % capacity;
    }

    private void resize(int newCapacity) {
        hashNode[] oldEntries = arr;

        capacity = Math.max(1, newCapacity);
        arr = new hashNode[capacity];
        size = 0;

        for (hashNode node : oldEntries) {
            if (node != null && !node.deleted) {
                insertNode(node.key, node.value);
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
