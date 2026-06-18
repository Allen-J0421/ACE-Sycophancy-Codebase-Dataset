class MinHeap {

    private static final int ROOT_INDEX = 0;
    private static final int EMPTY_HEAP_VALUE = Integer.MAX_VALUE;
    private static final int DELETED_KEY_VALUE = Integer.MIN_VALUE;

    private final int[] heap;

    private int size;

    public MinHeap(int n) {
        heap = new int[n];
        size = 0;
    }

    private int valueAt(int index) {
        return heap[index];
    }

    private void setValue(int index, int value) {
        heap[index] = value;
    }

    private void swap(int firstIndex, int secondIndex) {
        int temp = valueAt(firstIndex);
        setValue(firstIndex, valueAt(secondIndex));
        setValue(secondIndex, temp);
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
        return size == heap.length;
    }

    private boolean isEmpty() {
        return size <= 0;
    }

    private boolean containsIndex(int index) {
        return index >= 0 && index < size;
    }

    private boolean hasSmallerValue(int firstIndex, int secondIndex) {
        return valueAt(firstIndex) < valueAt(secondIndex);
    }

    private int smallestIndexBetween(int firstIndex, int secondIndex) {
        if (containsIndex(firstIndex) && hasSmallerValue(firstIndex, secondIndex)) {
            return firstIndex;
        }
        return secondIndex;
    }

    private int smallestChildOrSelfIndex(int index) {
        int smallest = smallestIndexBetween(leftChildIndex(index), index);
        return smallestIndexBetween(rightChildIndex(index), smallest);
    }

    private void bubbleUp(int index) {
        while (index != ROOT_INDEX) {
            int parent = parentIndex(index);
            if (!hasSmallerValue(index, parent)) {
                return;
            }

            swap(index, parent);
            index = parent;
        }
    }

    private int removeLastElement() {
        size--;
        return valueAt(size);
    }

    private int append(int key) {
        int insertIndex = size;
        setValue(insertIndex, key);
        size++;
        return insertIndex;
    }

    public boolean insertKey(int key) {
        if (isFull()) {
            return false;
        }

        int insertIndex = append(key);
        bubbleUp(insertIndex);
        return true;
    }

    public void decreaseKey(int index, int newValue) {
        setValue(index, newValue);
        bubbleUp(index);
    }

    public int getMin() {
        return valueAt(ROOT_INDEX);
    }

    public int extractMin() {
        if (isEmpty()) {
            return EMPTY_HEAP_VALUE;
        }

        int minValue = valueAt(ROOT_INDEX);

        setValue(ROOT_INDEX, removeLastElement());
        if (!isEmpty()) {
            bubbleDown(ROOT_INDEX);
        }

        return minValue;
    }

    public void deleteKey(int index) {
        decreaseKey(index, DELETED_KEY_VALUE);
        extractMin();
    }

    private void bubbleDown(int index) {
        while (true) {
            int smallest = smallestChildOrSelfIndex(index);
            if (smallest == index) {
                return;
            }

            swap(index, smallest);
            index = smallest;
        }
    }

    public void increaseKey(int index, int newValue) {
        setValue(index, newValue);
        bubbleDown(index);
    }

    public void changeValueOnAKey(int index, int newValue) {
        int currentValue = valueAt(index);
        if (currentValue == newValue) {
            return;
        }
        if (currentValue < newValue) {
            increaseKey(index, newValue);
        } else {
            decreaseKey(index, newValue);
        }
    }
}

class MinHeapTest {
    public static void main(String[] args) {
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
