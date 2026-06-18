import java.lang.*;

class hashNode {
    int key;
    int value;

    public hashNode(int key, int value) {
        this.key = key;
        this.value = value;
    }
}

class hashMap {
    hashNode[] arr;
    int capacity;
    int size;
    hashNode dummy;

    public hashMap() {
        capacity = 20;
        size = 0;
        arr = new hashNode[capacity];
        dummy = new hashNode(-1, -1);
    }

    int hashCode(int key) {
        return key % capacity;
    }

    void insertNode(int key, int value) {
        hashNode temp = new hashNode(key, value);
        int hashIndex = hashCode(key);

        while (arr[hashIndex] != null &&
               arr[hashIndex].key != key &&
               arr[hashIndex].key != -1) {
            hashIndex++;
            hashIndex %= capacity;
        }

        if (arr[hashIndex] == null || arr[hashIndex].key == -1)
            size++;
        arr[hashIndex] = temp;
    }

    int deleteNode(int key) {
        int hashIndex = hashCode(key);

        while (arr[hashIndex] != null) {
            if (arr[hashIndex].key == key) {
                hashNode temp = arr[hashIndex];
                arr[hashIndex] = dummy;
                size--;
                return temp.value;
            }
            hashIndex++;
            hashIndex %= capacity;
        }

        return -1;
    }

    int get(int key) {
        int hashIndex = hashCode(key);
        int counter = 0;

        while (arr[hashIndex] != null) {
            if (counter++ > capacity)
                return -1;

            if (arr[hashIndex].key == key)
                return arr[hashIndex].value;
            hashIndex++;
            hashIndex %= capacity;
        }

        return -1;
    }

    int sizeofMap() {
        return size;
    }

    boolean isEmpty() {
        return size == 0;
    }

    void display() {
        for (int i = 0; i < capacity; i++) {
            if (arr[i] != null && arr[i].key != -1) {
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
