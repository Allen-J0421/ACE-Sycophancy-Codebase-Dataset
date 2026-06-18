import java.util.NoSuchElementException;

public class MinHeap {
    private final int[] heap;
    private int size;

    public MinHeap(int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException("capacity must be non-negative");
        }

        heap = new int[capacity];
        size = 0;
    }

    public boolean offer(int value) {
        if (isFull()) {
            return false;
        }

        int insertIndex = size;
        heap[insertIndex] = value;
        size++;
        siftUp(insertIndex);
        return true;
    }

    public boolean insertKey(int value) {
        return offer(value);
    }

    public int peek() {
        ensureNotEmpty();
        return heap[0];
    }

    public int getMin() {
        return peek();
    }

    public int removeMin() {
        ensureNotEmpty();

        int min = heap[0];
        size--;
        if (!isEmpty()) {
            heap[0] = heap[size];
            siftDown(0);
        }
        return min;
    }

    public int extractMin() {
        if (isEmpty()) {
            return Integer.MAX_VALUE;
        }

        return removeMin();
    }

    public int removeAt(int index) {
        validateOccupiedIndex(index);

        int removedValue = heap[index];
        int lastIndex = size - 1;
        size--;
        if (index == lastIndex) {
            return removedValue;
        }

        heap[index] = heap[lastIndex];
        restoreHeapAt(index);
        return removedValue;
    }

    public void deleteKey(int index) {
        removeAt(index);
    }

    public void updateValue(int index, int newValue) {
        validateOccupiedIndex(index);

        int currentValue = heap[index];
        heap[index] = newValue;
        if (newValue < currentValue) {
            siftUp(index);
            return;
        }

        if (newValue > currentValue) {
            siftDown(index);
        }
    }

    public void decreaseKey(int index, int newValue) {
        validateOccupiedIndex(index);
        ensureValueDecreases(index, newValue);
        updateValue(index, newValue);
    }

    public void increaseKey(int index, int newValue) {
        validateOccupiedIndex(index);
        ensureValueIncreases(index, newValue);
        updateValue(index, newValue);
    }

    public void changeValueOnAKey(int index, int newValue) {
        updateValue(index, newValue);
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean isFull() {
        return size == heap.length;
    }

    public int size() {
        return size;
    }

    public int capacity() {
        return heap.length;
    }

    public int valueAt(int index) {
        validateOccupiedIndex(index);
        return heap[index];
    }

    private void siftUp(int index) {
        while (index > 0) {
            int parentIndex = parent(index);
            if (heap[parentIndex] <= heap[index]) {
                return;
            }

            swap(index, parentIndex);
            index = parentIndex;
        }
    }

    private void siftDown(int index) {
        while (true) {
            int leftChild = left(index);
            int rightChild = right(index);
            int smallest = index;

            if (leftChild < size && heap[leftChild] < heap[smallest]) {
                smallest = leftChild;
            }

            if (rightChild < size && heap[rightChild] < heap[smallest]) {
                smallest = rightChild;
            }

            if (smallest == index) {
                return;
            }

            swap(index, smallest);
            index = smallest;
        }
    }

    private void restoreHeapAt(int index) {
        if (index > 0 && heap[index] < heap[parent(index)]) {
            siftUp(index);
            return;
        }

        siftDown(index);
    }

    private void swap(int firstIndex, int secondIndex) {
        int temp = heap[firstIndex];
        heap[firstIndex] = heap[secondIndex];
        heap[secondIndex] = temp;
    }

    private void ensureNotEmpty() {
        if (isEmpty()) {
            throw new NoSuchElementException("heap is empty");
        }
    }

    private void validateOccupiedIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(
                "index " + index + " is outside heap size " + size
            );
        }
    }

    private void ensureValueDecreases(int index, int newValue) {
        if (newValue > heap[index]) {
            throw new IllegalArgumentException(
                "new value " + newValue + " is greater than current value " + heap[index]
            );
        }
    }

    private void ensureValueIncreases(int index, int newValue) {
        if (newValue < heap[index]) {
            throw new IllegalArgumentException(
                "new value " + newValue + " is less than current value " + heap[index]
            );
        }
    }

    private int parent(int index) {
        return (index - 1) / 2;
    }

    private int left(int index) {
        return index * 2 + 1;
    }

    private int right(int index) {
        return index * 2 + 2;
    }
}
