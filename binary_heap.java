import java.util.*;

class MinHeap<T extends Comparable<T>> {

    private T[] heapArray;

    private int capacity;

    private int current_heap_size;

    @SuppressWarnings("unchecked")
    public MinHeap(int n) {
        capacity = n;
        heapArray = (T[]) new Comparable[capacity];
        current_heap_size = 0;
    }

    private void swap(T[] arr, int a, int b) {
        T temp = arr[a];
        arr[a] = arr[b];
        arr[b] = temp;
    }

    private int parent(int key) {
        return (key - 1) / 2;
    }

    private int left(int key) {
        return 2 * key + 1;
    }

    private int right(int key) {
        return 2 * key + 2;
    }

    public boolean insertKey(T key) {
        if (current_heap_size == capacity) {

            return false;
        }

        int i = current_heap_size;
        heapArray[i] = key;
        current_heap_size++;

        while (i != 0 && heapArray[i].compareTo(heapArray[parent(i)]) < 0) {
            swap(heapArray, i, parent(i));
            i = parent(i);
        }
        return true;
    }

    public void decreaseKey(int index, T new_val) {
        heapArray[index] = new_val;

        while (index != 0 && heapArray[index].compareTo(heapArray[parent(index)]) < 0) {
            swap(heapArray, index, parent(index));
            index = parent(index);
        }
    }

    public T getMin() {
        return heapArray[0];
    }

    public T extractMin() {
        if (current_heap_size <= 0) {
            return null;
        }

        if (current_heap_size == 1) {
            current_heap_size--;
            return heapArray[0];
        }

        T root = heapArray[0];

        heapArray[0] = heapArray[current_heap_size - 1];
        current_heap_size--;
        MinHeapify(0);

        return root;
    }

    public void deleteKey(int index) {
        heapArray[index] = heapArray[current_heap_size - 1];
        current_heap_size--;
        if (index < current_heap_size) {
            if (index > 0 && heapArray[index].compareTo(heapArray[parent(index)]) < 0) {
                int i = index;
                while (i != 0 && heapArray[i].compareTo(heapArray[parent(i)]) < 0) {
                    swap(heapArray, i, parent(i));
                    i = parent(i);
                }
            } else {
                MinHeapify(index);
            }
        }
    }

    private void MinHeapify(int key) {
        int l = left(key);
        int r = right(key);

        int smallest = key;
        if (l < current_heap_size && heapArray[l].compareTo(heapArray[smallest]) < 0) {
            smallest = l;
        }
        if (r < current_heap_size && heapArray[r].compareTo(heapArray[smallest]) < 0) {
            smallest = r;
        }

        if (smallest != key) {
            swap(heapArray, key, smallest);
            MinHeapify(smallest);
        }
    }

    public void increaseKey(int index, T new_val) {
        heapArray[index] = new_val;
        MinHeapify(index);
    }

    public void changeValueOnAKey(int index, T new_val) {
        if (heapArray[index].equals(new_val)) {
            return;
        }
        if (heapArray[index].compareTo(new_val) < 0) {
            increaseKey(index, new_val);
        } else {
            decreaseKey(index, new_val);
        }
    }
}

class MinHeapTest {
    public static void main(String[] args) {
        MinHeap<Integer> h = new MinHeap<>(11);
        h.insertKey(3);
        h.insertKey(2);
        h.deleteKey(1);
        h.insertKey(15);
        h.insertKey(5);
        h.insertKey(4);
        h.insertKey(45);
        System.out.print(h.extractMin() + " ");
        System.out.print(h.getMin() + " ");

        h.decreaseKey(2, 1);
        System.out.print(h.getMin());
    }
}
