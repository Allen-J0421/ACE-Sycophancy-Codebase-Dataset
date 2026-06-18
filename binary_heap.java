class MinHeap {

    private final int[] elements;

    private final int capacity;

    private int size;

    public MinHeap(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Heap capacity must be non-negative");
        }
        capacity = n;
        elements = new int[capacity];
        size = 0;
    }

    private void swap(int firstIndex, int secondIndex) {
        int temp = elements[firstIndex];
        elements[firstIndex] = elements[secondIndex];
        elements[secondIndex] = temp;
    }

    private int parentIndex(int index) {
        return (index - 1) / 2;
    }

    private int leftChildIndex(int index) {
        return 2 * index + 1;
    }

    private int rightChildIndex(int index) {
        return 2 * index + 2;
    }

    private boolean isFull() {
        return size == capacity;
    }

    private boolean isEmptyInternal() {
        return size == 0;
    }

    private void validateIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(
                "Index " + index + " is outside the heap size " + size
            );
        }
    }

    private void bubbleUp(int index) {
        while (index != 0 && elements[index] < elements[parentIndex(index)]) {
            int parent = parentIndex(index);
            swap(index, parent);
            index = parent;
        }
    }

    private void bubbleDown(int index) {
        while (true) {
            int smallest = index;
            int left = leftChildIndex(index);
            int right = rightChildIndex(index);

            if (left < size && elements[left] < elements[smallest]) {
                smallest = left;
            }
            if (right < size && elements[right] < elements[smallest]) {
                smallest = right;
            }

            if (smallest == index) {
                return;
            }

            swap(index, smallest);
            index = smallest;
        }
    }

    private void replaceKey(int index, int newVal) {
        int oldVal = elements[index];
        elements[index] = newVal;

        if (newVal < oldVal) {
            bubbleUp(index);
        } else if (newVal > oldVal) {
            bubbleDown(index);
        }
    }

    public boolean insertKey(int key) {
        if (isFull()) {
            return false;
        }

        elements[size] = key;
        bubbleUp(size);
        size++;
        return true;
    }

    public void decreaseKey(int key, int newVal) {
        validateIndex(key);
        if (newVal > elements[key]) {
            throw new IllegalArgumentException("New value must not be greater than the current value");
        }
        replaceKey(key, newVal);
    }

    public int getMin() {
        if (isEmptyInternal()) {
            return Integer.MAX_VALUE;
        }
        return elements[0];
    }

    public int extractMin() {
        if (isEmptyInternal()) {
            return Integer.MAX_VALUE;
        }

        if (size == 1) {
            size--;
            return elements[0];
        }

        int root = elements[0];
        elements[0] = elements[size - 1];
        size--;
        bubbleDown(0);
        return root;
    }

    public void deleteKey(int key) {
        validateIndex(key);
        replaceKey(key, Integer.MIN_VALUE);
        extractMin();
    }

    public void increaseKey(int key, int newVal) {
        validateIndex(key);
        if (newVal < elements[key]) {
            throw new IllegalArgumentException("New value must not be less than the current value");
        }
        replaceKey(key, newVal);
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return isEmptyInternal();
    }

    public void changeValueOnAKey(int key, int newVal) {
        validateIndex(key);
        if (elements[key] == newVal) {
            return;
        }
        replaceKey(key, newVal);
    }
}

class MinHeapTest {
    private static void runSanityChecks() {
        MinHeap empty = new MinHeap(0);
        if (empty.getMin() != Integer.MAX_VALUE) {
            throw new AssertionError("Empty heap should report Integer.MAX_VALUE");
        }
        if (empty.extractMin() != Integer.MAX_VALUE) {
            throw new AssertionError("Empty heap extraction should report Integer.MAX_VALUE");
        }

        MinHeap heap = new MinHeap(4);
        if (!heap.insertKey(8) || !heap.insertKey(3) || !heap.insertKey(5)) {
            throw new AssertionError("Expected inserts to succeed");
        }
        heap.changeValueOnAKey(0, 1);
        if (heap.getMin() != 1) {
            throw new AssertionError("Key update should restore heap order");
        }
        if (heap.size() != 3) {
            throw new AssertionError("Heap size should reflect inserted elements");
        }
    }

    public static void main(String[] args) {
        runSanityChecks();

        MinHeap h = new MinHeap(11);
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
