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

    public boolean insertKey(int value) {
        if (size == heap.length) {
            return false;
        }

        heap[size] = value;
        siftUp(size);
        size++;
        return true;
    }

    public void decreaseKey(int index, int newValue) {
        validateOccupiedIndex(index);
        heap[index] = newValue;
        siftUp(index);
    }

    public int getMin() {
        ensureNotEmpty();
        return heap[0];
    }

    public int extractMin() {
        if (isEmpty()) {
            return Integer.MAX_VALUE;
        }

        int min = heap[0];
        size--;
        if (!isEmpty()) {
            heap[0] = heap[size];
            siftDown(0);
        }
        return min;
    }

    public void deleteKey(int index) {
        validateOccupiedIndex(index);
        decreaseKey(index, Integer.MIN_VALUE);
        extractMin();
    }

    public void increaseKey(int index, int newValue) {
        validateOccupiedIndex(index);
        heap[index] = newValue;
        siftDown(index);
    }

    public void changeValueOnAKey(int index, int newValue) {
        validateOccupiedIndex(index);
        if (heap[index] == newValue) {
            return;
        }

        if (newValue < heap[index]) {
            decreaseKey(index, newValue);
            return;
        }

        increaseKey(index, newValue);
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
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
