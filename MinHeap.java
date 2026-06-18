import java.util.Arrays;
import java.util.NoSuchElementException;

public final class MinHeap implements LegacyMinHeapApi {
    private static final int DEFAULT_CAPACITY = 10;

    private int[] elements;
    private int size;

    public MinHeap() {
        this(new int[DEFAULT_CAPACITY], 0, false);
    }

    public MinHeap(int initialCapacity) {
        this(new int[validateInitialCapacity(initialCapacity)], 0, false);
    }

    public MinHeap(int[] values) {
        this(copyInput(values), values.length, true);
    }

    public static MinHeap from(int... values) {
        return new MinHeap(values);
    }

    public static MinHeap copyOf(IntHeap other) {
        if (other == null) {
            throw new IllegalArgumentException("other heap must not be null");
        }

        return new MinHeap(other.toArray());
    }

    private MinHeap(int[] elements, int size, boolean needsHeapify) {
        this.elements = elements;
        this.size = size;
        if (needsHeapify) {
            rebuildHeap();
        }
    }

    @Override
    public boolean offer(int value) {
        ensureCapacity(size + 1);
        elements[size] = value;
        siftUp(size);
        size++;
        return true;
    }

    @Override
    public void addAll(int... values) {
        if (values == null) {
            throw new IllegalArgumentException("values must not be null");
        }

        if (values.length == 0) {
            return;
        }

        int originalSize = size;
        ensureCapacity(originalSize + values.length);
        System.arraycopy(values, 0, elements, originalSize, values.length);
        size += values.length;
        rebuildHeap();
    }

    @Override
    public int peek() {
        ensureNotEmpty();
        return elements[0];
    }

    @Override
    public int removeMin() {
        ensureNotEmpty();
        return removeAt(0);
    }

    @Override
    public int replaceMin(int value) {
        ensureNotEmpty();
        int previousMin = elements[0];
        elements[0] = value;
        siftDown(0);
        return previousMin;
    }

    @Override
    public int removeAt(int index) {
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

    @Override
    public void updateValue(int index, int newValue) {
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

    @Override
    public void clear() {
        Arrays.fill(elements, 0, size, 0);
        size = 0;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public int capacity() {
        return elements.length;
    }

    @Override
    public int valueAt(int index) {
        validateOccupiedIndex(index);
        return elements[index];
    }

    @Override
    public int[] toArray() {
        return Arrays.copyOf(elements, size);
    }

    private void rebuildHeap() {
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

    private int parentIndex(int index) {
        return (index - 1) / 2;
    }

    private int leftChildIndex(int index) {
        return index * 2 + 1;
    }

    private int rightChildIndex(int index) {
        return index * 2 + 2;
    }

    private static int validateInitialCapacity(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("initial capacity must be non-negative");
        }

        return initialCapacity;
    }

    private static int[] copyInput(int[] values) {
        if (values == null) {
            throw new IllegalArgumentException("values must not be null");
        }

        return Arrays.copyOf(values, values.length);
    }
}
