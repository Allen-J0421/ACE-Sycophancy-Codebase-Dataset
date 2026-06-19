import java.util.NoSuchElementException;

class MinHeap {

    private final int[] heap;
    private final int capacity;
    private int size;

    public MinHeap(int capacity) {
        this.capacity = capacity;
        this.heap = new int[capacity];
        this.size = 0;
    }

    private void swap(int a, int b) {
        int temp = heap[a];
        heap[a] = heap[b];
        heap[b] = temp;
    }

    private int parent(int i) {
        return (i - 1) / 2;
    }

    private int left(int i) {
        return 2 * i + 1;
    }

    private int right(int i) {
        return 2 * i + 2;
    }

    public boolean insertKey(int key) {
        if (size == capacity) {
            return false;
        }

        int i = size;
        heap[i] = key;
        size++;

        while (i != 0 && heap[i] < heap[parent(i)]) {
            swap(i, parent(i));
            i = parent(i);
        }
        return true;
    }

    public void decreaseKey(int i, int newVal) {
        heap[i] = newVal;
        while (i != 0 && heap[i] < heap[parent(i)]) {
            swap(i, parent(i));
            i = parent(i);
        }
    }

    public int getMin() {
        if (size == 0) {
            throw new NoSuchElementException("Heap is empty");
        }
        return heap[0];
    }

    public int extractMin() {
        if (size <= 0) {
            return Integer.MAX_VALUE;
        }
        if (size == 1) {
            size--;
            return heap[0];
        }

        int root = heap[0];
        heap[0] = heap[size - 1];
        size--;
        heapify(0);
        return root;
    }

    public void deleteKey(int i) {
        decreaseKey(i, Integer.MIN_VALUE);
        extractMin();
    }

    private void heapify(int i) {
        int l = left(i);
        int r = right(i);
        int smallest = i;

        if (l < size && heap[l] < heap[smallest]) {
            smallest = l;
        }
        if (r < size && heap[r] < heap[smallest]) {
            smallest = r;
        }
        if (smallest != i) {
            swap(i, smallest);
            heapify(smallest);
        }
    }

    public void increaseKey(int i, int newVal) {
        heap[i] = newVal;
        heapify(i);
    }

    public void changeKey(int i, int newVal) {
        if (heap[i] == newVal) {
            return;
        }
        if (heap[i] < newVal) {
            increaseKey(i, newVal);
        } else {
            decreaseKey(i, newVal);
        }
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }
}
