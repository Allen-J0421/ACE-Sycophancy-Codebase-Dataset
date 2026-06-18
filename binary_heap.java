class MinHeap {

    private static final int EMPTY_HEAP_VALUE = Integer.MAX_VALUE;
    private static final int DELETED_KEY_VALUE = Integer.MIN_VALUE;

    private final int[] heap;

    private int size;

    public MinHeap(int n) {
        heap = new int[n];
        size = 0;
    }

    private void swap(int firstIndex, int secondIndex) {
        int temp = heap[firstIndex];
        heap[firstIndex] = heap[secondIndex];
        heap[secondIndex] = temp;
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

    private void bubbleUp(int index) {
        while (index != 0) {
            int parent = parentIndex(index);
            if (heap[index] >= heap[parent]) {
                return;
            }

            swap(index, parent);
            index = parent;
        }
    }

    private int removeLastElement() {
        size--;
        return heap[size];
    }

    private int append(int key) {
        int insertIndex = size;
        heap[insertIndex] = key;
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
        heap[index] = newValue;
        bubbleUp(index);
    }

    public int getMin() {
        return heap[0];
    }

    public int extractMin() {
        if (isEmpty()) {
            return EMPTY_HEAP_VALUE;
        }

        int minValue = heap[0];

        heap[0] = removeLastElement();
        if (!isEmpty()) {
            bubbleDown(0);
        }

        return minValue;
    }

    public void deleteKey(int index) {
        decreaseKey(index, DELETED_KEY_VALUE);
        extractMin();
    }

    private void bubbleDown(int index) {
        while (true) {
            int left = leftChildIndex(index);
            int right = rightChildIndex(index);
            int smallest = index;

            if (left < size && heap[left] < heap[smallest]) {
                smallest = left;
            }
            if (right < size && heap[right] < heap[smallest]) {
                smallest = right;
            }
            if (smallest == index) {
                return;
            }

            swap(index, smallest);
            index = smallest;
        }
    }

    public void increaseKey(int index, int newValue) {
        heap[index] = newValue;
        bubbleDown(index);
    }

    public void changeValueOnAKey(int index, int newValue) {
        if (heap[index] == newValue) {
            return;
        }
        if (heap[index] < newValue) {
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
