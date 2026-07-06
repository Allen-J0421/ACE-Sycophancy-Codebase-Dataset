class HashMap implements HashMapOperations {
    private HashNode[] table;
    private int capacity;
    private int size;
    private static final HashNode DUMMY = new HashNode(-1, -1);

    public HashMap() {
        capacity = 20;
        size = 0;
        table = new HashNode[capacity];
    }

    private int hash(int key) {
        return key % capacity;
    }

    @Override
    public void insertNode(int key, int value) {
        HashNode temp = new HashNode(key, value);
        int hashIndex = hash(key);

        while (table[hashIndex] != null &&
               table[hashIndex].getKey() != key &&
               table[hashIndex].getKey() != -1) {
            hashIndex++;
            hashIndex %= capacity;
        }

        if (table[hashIndex] == null || table[hashIndex].getKey() == -1)
            size++;
        table[hashIndex] = temp;
    }

    @Override
    public int deleteNode(int key) {
        int hashIndex = hash(key);

        while (table[hashIndex] != null) {
            if (table[hashIndex].getKey() == key) {
                HashNode temp = table[hashIndex];
                table[hashIndex] = DUMMY;
                size--;
                return temp.getValue();
            }
            hashIndex++;
            hashIndex %= capacity;
        }

        return -1;
    }

    @Override
    public int get(int key) {
        int hashIndex = hash(key);
        int counter = 0;

        while (table[hashIndex] != null) {
            if (counter++ > capacity)
                return -1;

            if (table[hashIndex].getKey() == key)
                return table[hashIndex].getValue();
            hashIndex++;
            hashIndex %= capacity;
        }

        return -1;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public void display() {
        for (int i = 0; i < capacity; i++) {
            if (table[i] != null && table[i].getKey() != -1) {
                System.out.println(table[i].getKey() + " " + table[i].getValue());
            }
        }
    }
}
