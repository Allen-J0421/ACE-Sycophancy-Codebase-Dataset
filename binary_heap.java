class MinHeap {

    private static final int EMPTY_HEAP_VALUE = Integer.MAX_VALUE;
    private static final int DELETED_KEY_VALUE = Integer.MIN_VALUE;

    private final int[] heapArray;

    private int size;

    public MinHeap(int n) {
        heapArray = new int[n];
        size = 0;
    }

    private void swap(int firstIndex, int secondIndex) {
        int temp = heapArray[firstIndex];
        heapArray[firstIndex] = heapArray[secondIndex];
        heapArray[secondIndex] = temp;
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
        return size == heapArray.length;
    }

    private boolean isEmpty() {
        return size <= 0;
    }

    private boolean hasSingleElement() {
        return size == 1;
    }

    private void bubbleUp(int index) {
        while (index != 0) {
            int parent = parentIndex(index);
            if (heapArray[index] >= heapArray[parent]) {
                return;
            }

            swap(index, parent);
            index = parent;
        }
    }

    public boolean insertKey(int key) {
        if (isFull()) {
            return false;
        }

        int insertIndex = size;
        heapArray[insertIndex] = key;
        size++;

        bubbleUp(insertIndex);
        return true;
    }

    public void decreaseKey(int index, int newValue) {
        heapArray[index] = newValue;
        bubbleUp(index);
    }

    public int getMin() {
        return heapArray[0];
    }

    public int extractMin() {
        if (isEmpty()) {
            return EMPTY_HEAP_VALUE;
        }

        if (hasSingleElement()) {
            size--;
            return heapArray[0];
        }

        int minValue = heapArray[0];

        heapArray[0] = heapArray[size - 1];
        size--;
        bubbleDown(0);

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

            if (left < size && heapArray[left] < heapArray[smallest]) {
                smallest = left;
            }
            if (right < size && heapArray[right] < heapArray[smallest]) {
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
        heapArray[index] = newValue;
        bubbleDown(index);
    }

    public void changeValueOnAKey(int index, int newValue) {
        if (heapArray[index] == newValue) {
            return;
        }
        if (heapArray[index] < newValue) {
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
