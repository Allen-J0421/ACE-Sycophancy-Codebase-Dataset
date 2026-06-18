class HashEntry {
    final int key;
    int value;
    boolean deleted;

    HashEntry(int key, int value) {
        this.key = key;
        this.value = value;
        this.deleted = false;
    }

    boolean isActive() {
        return !deleted;
    }

    boolean matches(int k) {
        return key == k && !deleted;
    }
}

interface IntMap {
    void put(int key, int value);
    int get(int key);
    int remove(int key);
    int size();
    boolean isEmpty();
}

class LinearProbingHashMap implements IntMap {
    private static final int DEFAULT_CAPACITY = 16;
    private static final float LOAD_FACTOR = 0.75f;
    private static final int NOT_FOUND = -1;

    private HashEntry[] table;
    private int size;
    private int capacity;

    public LinearProbingHashMap() {
        this.capacity = DEFAULT_CAPACITY;
        this.size = 0;
        this.table = new HashEntry[capacity];
    }

    public void put(int key, int value) {
        if (shouldResize()) {
            resize();
        }

        int index = findSlot(key);
        if (index < 0) {
            return;
        }

        if (table[index] == null || !table[index].isActive()) {
            size++;
        }
        table[index] = new HashEntry(key, value);
    }

    public int get(int key) {
        int index = findExisting(key);
        return index >= 0 ? table[index].value : NOT_FOUND;
    }

    public int remove(int key) {
        int index = findExisting(key);
        if (index >= 0) {
            int value = table[index].value;
            table[index].deleted = true;
            size--;
            return value;
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
        for (HashEntry entry : table) {
            if (entry != null && entry.isActive()) {
                System.out.println(entry.key + " " + entry.value);
            }
        }
    }

    private int hash(int key) {
        return Math.abs(key) % capacity;
    }

    private int findSlot(int key) {
        int index = hash(key);
        int firstDeleted = -1;

        for (int i = 0; i < capacity; i++) {
            int probeIndex = (index + i) % capacity;
            HashEntry entry = table[probeIndex];

            if (entry == null) {
                return firstDeleted >= 0 ? firstDeleted : probeIndex;
            }
            if (entry.key == key && entry.isActive()) {
                return probeIndex;
            }
            if (entry.deleted && firstDeleted < 0) {
                firstDeleted = probeIndex;
            }
        }
        return -1;
    }

    private int findExisting(int key) {
        int index = hash(key);

        for (int i = 0; i < capacity; i++) {
            int probeIndex = (index + i) % capacity;
            HashEntry entry = table[probeIndex];

            if (entry == null) {
                return -1;
            }
            if (entry.matches(key)) {
                return probeIndex;
            }
        }
        return -1;
    }

    private boolean shouldResize() {
        return size >= capacity * LOAD_FACTOR;
    }

    private void resize() {
        HashEntry[] oldTable = table;
        capacity *= 2;
        table = new HashEntry[capacity];
        size = 0;

        for (HashEntry entry : oldTable) {
            if (entry != null && entry.isActive()) {
                put(entry.key, entry.value);
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
