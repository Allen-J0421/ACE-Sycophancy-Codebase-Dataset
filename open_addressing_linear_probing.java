class HashNode {
    private int key;
    private int value;

    public HashNode(int key, int value) {
        this.key = key;
        this.value = value;
    }

    public int getKey() {
        return key;
    }

    public int getValue() {
        return value;
    }
}

class HashMap {
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

    public int getSize() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void display() {
        for (int i = 0; i < capacity; i++) {
            if (table[i] != null && table[i].getKey() != -1) {
                System.out.println(table[i].getKey() + " " + table[i].getValue());
            }
        }
    }

    public static void main(String[] args) {
        HashMap h = new HashMap();
        h.insertNode(1, 1);
        h.insertNode(2, 2);
        h.insertNode(2, 3);
        h.display();
        System.out.println(h.getSize());
        System.out.println(h.deleteNode(2));
        System.out.println(h.getSize());
        System.out.println(h.isEmpty());
        System.out.println(h.get(2));
    }
}
