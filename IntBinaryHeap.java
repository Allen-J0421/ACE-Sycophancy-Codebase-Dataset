import java.util.Arrays;
import java.util.NoSuchElementException;

final class IntBinaryHeap {
    private static final int DEFAULT_CAPACITY = 10;

    private int[] elements;
    private int size;

    IntBinaryHeap() {
        this(DEFAULT_CAPACITY);
    }

    IntBinaryHeap(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("initial capacity must be non-negative");
        }

        elements = new int[initialCapacity];
        size = 0;
    }

    IntBinaryHeap(int[] values) {
        if (values == null) {
            throw new IllegalArgumentException("values must not be null");
        }

        elements = Arrays.copyOf(values, values.length);
        size = values.length;
        heapify();
    }

    boolean offer(int value) {
        ensureCapacity(size + 1);
        elements[size] = value;
        siftUp(size);
        size++;
        return true;
    }

    int peek() {
        ensureNotEmpty();
        return elements[0];
    }

    int removeMin() {
        ensureNotEmpty();
        return removeAt(0);
    }

    int extractMin() {
        if (isEmpty()) {
            return Integer.MAX_VALUE;
        }

        return removeMin();
    }

    int removeAt(int index) {
        validateOccupiedIndex(index);

        int removedValue = elements[index];
        int lastIndex = size - 1;
        int lastValue = elements[lastIndex];
        elements[lastIndex] = 0;
        size--;
        if (index == lastIndex) {
            return removedValue;
        }

        elements[index] = lastValue;
        restoreHeapAt(index);
        return removedValue;
    }

    void updateValue(int index, int newValue) {
        validateOccupiedIndex(index);

        int currentValue = elements[index];
        elements[index] = newValue;
        if (newValue < currentValue) {
            siftUp(index);
            return;
        }

        if (newValue > currentValue) {
            siftDown(index);
        }
    }

    void decreaseKey(int index, int newValue) {
        validateOccupiedIndex(index);
        ensureValueDecreases(index, newValue);
        updateValue(index, newValue);
    }

    void increaseKey(int index, int newValue) {
        validateOccupiedIndex(index);
        ensureValueIncreases(index, newValue);
        updateValue(index, newValue);
    }

    void clear() {
        Arrays.fill(elements, 0, size, 0);
        size = 0;
    }

    boolean isEmpty() {
        return size == 0;
    }

    boolean isFull() {
        return size == elements.length;
    }

    int size() {
        return size;
    }

    int capacity() {
        return elements.length;
    }

    int valueAt(int index) {
        validateOccupiedIndex(index);
        return elements[index];
    }

    int[] toArray() {
        return Arrays.copyOf(elements, size);
    }

    private void heapify() {
        for (int index = parentIndex(size - 1); index >= 0; index--) {
            siftDown(index);
        }
    }

    private void ensureCapacity(int requiredCapacity) {
        if (requiredCapacity <= elements.length) {
            return;
        }

        int newCapacity = elements.length == 0 ? 1 : elements.length;
        while (newCapacity < requiredCapacity) {
            newCapacity *= 2;
        }

        elements = Arrays.copyOf(elements, newCapacity);
    }

    private void siftUp(int index) {
        while (index > 0) {
            int parentIndex = parentIndex(index);
            if (elements[parentIndex] <= elements[index]) {
                return;
            }

            swap(index, parentIndex);
            index = parentIndex;
        }
    }

    private void siftDown(int index) {
        while (true) {
            int leftChildIndex = leftChildIndex(index);
            int rightChildIndex = rightChildIndex(index);
            int smallestIndex = index;

            if (leftChildIndex < size && elements[leftChildIndex] < elements[smallestIndex]) {
                smallestIndex = leftChildIndex;
            }

            if (rightChildIndex < size && elements[rightChildIndex] < elements[smallestIndex]) {
                smallestIndex = rightChildIndex;
            }

            if (smallestIndex == index) {
                return;
            }

            swap(index, smallestIndex);
            index = smallestIndex;
        }
    }

    private void restoreHeapAt(int index) {
        if (index > 0 && elements[index] < elements[parentIndex(index)]) {
            siftUp(index);
            return;
        }

        siftDown(index);
    }

    private void swap(int firstIndex, int secondIndex) {
        int temp = elements[firstIndex];
        elements[firstIndex] = elements[secondIndex];
        elements[secondIndex] = temp;
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
        if (newValue > elements[index]) {
            throw new IllegalArgumentException(
                "new value " + newValue + " is greater than current value " + elements[index]
            );
        }
    }

    private void ensureValueIncreases(int index, int newValue) {
        if (newValue < elements[index]) {
            throw new IllegalArgumentException(
                "new value " + newValue + " is less than current value " + elements[index]
            );
        }
    }

    private int parentIndex(int index) {
        return (index - 1) / 2;
    }

    private int leftChildIndex(int index) {
        return index * 2 + 1;
    }

    private int rightChildIndex(int index) {
        return index * 2 + 2;
    }
}
