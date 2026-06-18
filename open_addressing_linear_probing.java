class HashNode {
    int key;
    int value;

    public HashNode(int key, int value) {
        this.key = key;
        this.value = value;
    }
}

class LinearProbingHashMap {
    private static final int DEFAULT_CAPACITY = 20;
    private static final int DELETED_KEY = -1;
    private static final int NOT_FOUND = -1;

    private HashNode[] table;
    private int capacity;
    private int size;
    private HashNode deletedNode;

    public LinearProbingHashMap() {
        this.capacity = DEFAULT_CAPACITY;
        this.size = 0;
        this.table = new HashNode[capacity];
        this.deletedNode = new HashNode(DELETED_KEY, NOT_FOUND);
    }

    private int hash(int key) {
        return Math.abs(key) % capacity;
    }

    private int probe(int startIndex, int key, boolean findEmpty) {
        int index = startIndex;
        int probeCount = 0;

        while (probeCount < capacity) {
            if (table[index] == null) {
                return findEmpty ? index : NOT_FOUND;
            }
            if (table[index].key == key) {
                return index;
            }
            if (findEmpty && table[index].key == DELETED_KEY) {
                return index;
            }
            index = (index + 1) % capacity;
            probeCount++;
        }
        return NOT_FOUND;
    }

    public void put(int key, int value) {
        int hashIndex = hash(key);
        int index = probe(hashIndex, key, true);

        if (index != NOT_FOUND) {
            boolean isNewEntry = table[index] == null || table[index].key == DELETED_KEY;
            table[index] = new HashNode(key, value);
            if (isNewEntry) {
                size++;
            }
        }
    }

    public int remove(int key) {
        int hashIndex = hash(key);
        int index = probe(hashIndex, key, false);

        if (index != NOT_FOUND) {
            int value = table[index].value;
            table[index] = deletedNode;
            size--;
            return value;
        }
        return NOT_FOUND;
    }

    public int get(int key) {
        int hashIndex = hash(key);
        int index = probe(hashIndex, key, false);

        if (index != NOT_FOUND) {
            return table[index].value;
        }
        return NOT_FOUND;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void display() {
        for (int i = 0; i < capacity; i++) {
            if (table[i] != null && table[i].key != DELETED_KEY) {
                System.out.println(table[i].key + " " + table[i].value);
            }
        }
    }
}

class Demo {
    public static void main(String[] args) {
        LinearProbingHashMap map = new LinearProbingHashMap();

        map.put(1, 1);
        map.put(2, 2);
        map.put(2, 3);

        map.display();
        System.out.println("Size: " + map.size());
        System.out.println("Remove 2: " + map.remove(2));
        System.out.println("Size: " + map.size());
        System.out.println("Is empty: " + map.isEmpty());
        System.out.println("Get 2: " + map.get(2));
    }
}
