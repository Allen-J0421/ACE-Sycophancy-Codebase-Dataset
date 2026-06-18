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
        replaceKey(key, newVal);
    }

    public int peekMin() {
        if (isEmptyHeap()) {
            return Integer.MAX_VALUE;
        }
        return elements[0];
    }

    public int getMin() {
        return peekMin();
    }

    public int pollMin() {
        if (isEmptyHeap()) {
            return Integer.MAX_VALUE;
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
        replaceKey(key, newVal);
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return isEmptyHeap();
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
        if (empty.peekMin() != Integer.MAX_VALUE) {
            throw new AssertionError("Empty heap should report Integer.MAX_VALUE");
        }
        if (empty.pollMin() != Integer.MAX_VALUE) {
            throw new AssertionError("Empty heap extraction should report Integer.MAX_VALUE");
        }

        MinHeap heap = new MinHeap(4);
        if (!heap.offer(8) || !heap.offer(3) || !heap.offer(5)) {
            throw new AssertionError("Expected inserts to succeed");
        }
        heap.changeValueOnAKey(0, 1);
        if (heap.peekMin() != 1) {
            throw new AssertionError("Key update should restore heap order");
        }
        if (heap.size() != 3) {
            throw new AssertionError("Heap size should reflect inserted elements");
        }

        heap.deleteKey(1);
        if (heap.size() != 2) {
            throw new AssertionError("Delete should reduce heap size");
        }
        if (heap.peekMin() != 1) {
            throw new AssertionError("Delete should preserve the minimum element");
        }

        boolean rejected = false;
        try {
            heap.decreaseKey(0, 99);
        } catch (IllegalArgumentException expected) {
            rejected = true;
        }
        if (!rejected) {
            throw new AssertionError("decreaseKey should reject larger values");
        }
    }

    public static void main(String[] args) {
        runSanityChecks();

        MinHeap h = new MinHeap(11);
        h.offer(3);
        h.offer(2);
        h.deleteKey(1);
        h.offer(15);
        h.offer(5);
        h.offer(4);
        h.offer(45);
        System.out.print(h.pollMin() + " ");
        System.out.print(h.peekMin() + " ");

        h.decreaseKey(2, 1);
        System.out.print(h.peekMin());
    }
}
