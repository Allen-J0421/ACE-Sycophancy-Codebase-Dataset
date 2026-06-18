import java.util.Arrays;

public final class MinHeap {

    static final int EMPTY_VALUE = Integer.MAX_VALUE;

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

    public MinHeap(int[] values) {
        if (values == null) {
            throw new IllegalArgumentException("Heap values must not be null");
        }
        capacity = values.length;
        elements = Arrays.copyOf(values, values.length);
        size = values.length;
        heapify();
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

    private void updateKeyInternal(int index, int newVal) {
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

    private void validateKeyMovement(int index, int newVal, boolean decreasing) {
        int currentValue = elements[index];
        if (decreasing ? newVal > currentValue : newVal < currentValue) {
            throw new IllegalArgumentException(
                decreasing
                    ? "New value must not be greater than the current value"
                    : "New value must not be less than the current value"
            );
        }
    }

    private void repairHeapAt(int index) {
        if (index > 0 && elements[index] < elements[parentIndex(index)]) {
            bubbleUp(index);
        } else {
            bubbleDown(index);
        }
    }

    private void heapify() {
        for (int index = (size / 2) - 1; index >= 0; index--) {
            bubbleDown(index);
        }
    }

    private void removeAtInternal(int index) {
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

    public void decreaseKey(int key, int newVal) {
        validateIndex(key);
        validateKeyMovement(key, newVal, true);
        updateKeyInternal(key, newVal);
    }

    public int peek() {
        if (isEmptyHeap()) {
            return EMPTY_VALUE;
        }
        return elements[0];
    }

    public int poll() {
        if (isEmptyHeap()) {
            return EMPTY_VALUE;
        }

        int root = elements[0];
        removeAtInternal(0);
        return root;
    }

    public void increaseKey(int key, int newVal) {
        validateIndex(key);
        validateKeyMovement(key, newVal, false);
        updateKeyInternal(key, newVal);
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return isEmptyHeap();
    }

    public void updateKey(int key, int newVal) {
        validateIndex(key);
        updateKeyInternal(key, newVal);
    }

    public void removeAtIndex(int key) {
        removeAtInternal(key);
    }

    public int capacity() {
        return capacity;
    }
}
