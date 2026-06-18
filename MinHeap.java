public class MinHeap {

    static final int EMPTY_HEAP_VALUE = Integer.MAX_VALUE;

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

    private boolean isEmptyHeap() {
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
        int value = elements[index];
        while (index != 0) {
            int parent = parentIndex(index);
            if (elements[parent] <= value) {
                break;
            }
            elements[index] = elements[parent];
            index = parent;
        }
        elements[index] = value;
    }

    private void bubbleDown(int index) {
        int value = elements[index];
        while (true) {
            int left = leftChildIndex(index);
            if (left >= size) {
                break;
            }

            int right = rightChildIndex(index);
            int smallestChild = left;
            if (right < size && elements[right] < elements[left]) {
                smallestChild = right;
            }

            if (elements[smallestChild] >= value) {
                break;
            }

            elements[index] = elements[smallestChild];
            index = smallestChild;
        }
        elements[index] = value;
    }

    private void updateKeyAt(int index, int newVal) {
        int oldVal = elements[index];
        if (newVal == oldVal) {
            return;
        }

        elements[index] = newVal;
        if (newVal < oldVal) {
            bubbleUp(index);
        } else {
            bubbleDown(index);
        }
    }

    private void repairHeapAt(int index) {
        if (index > 0 && elements[index] < elements[parentIndex(index)]) {
            bubbleUp(index);
        } else {
            bubbleDown(index);
        }
    }

    private void removeAt(int index) {
        validateIndex(index);

        int lastIndex = size - 1;
        if (index == lastIndex) {
            size--;
            return;
        }

        elements[index] = elements[lastIndex];
        size--;
        repairHeapAt(index);
    }

    public boolean offer(int key) {
        if (isFull()) {
            return false;
        }

        elements[size] = key;
        bubbleUp(size);
        size++;
        return true;
    }

    public boolean insertKey(int key) {
        return offer(key);
    }

    public void decreaseKey(int key, int newVal) {
        validateIndex(key);
        if (newVal > elements[key]) {
            throw new IllegalArgumentException("New value must not be greater than the current value");
        }
        updateKeyAt(key, newVal);
    }

    public int peekMin() {
        if (isEmptyHeap()) {
            return EMPTY_HEAP_VALUE;
        }
        return elements[0];
    }

    public int getMin() {
        return peekMin();
    }

    public int pollMin() {
        if (isEmptyHeap()) {
            return EMPTY_HEAP_VALUE;
        }

        int root = elements[0];
        removeAt(0);
        return root;
    }

    public int extractMin() {
        return pollMin();
    }

    public void deleteKey(int key) {
        removeAt(key);
    }

    public void increaseKey(int key, int newVal) {
        validateIndex(key);
        if (newVal < elements[key]) {
            throw new IllegalArgumentException("New value must not be less than the current value");
        }
        updateKeyAt(key, newVal);
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return isEmptyHeap();
    }

    public void changeValueOnAKey(int key, int newVal) {
        validateIndex(key);
        updateKeyAt(key, newVal);
    }

    public int capacity() {
        return capacity;
    }
}
