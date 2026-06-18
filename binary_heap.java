class MinHeap {

    private final int[] heapArray;

    private final int capacity;

    private int size;

    public MinHeap(int n) {
        capacity = n;
        heapArray = new int[capacity];
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
        return size == capacity;
    }

    private boolean isEmpty() {
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
        while (index != 0 && heapArray[index] < heapArray[parentIndex(index)]) {
            int parent = parentIndex(index);
            swap(index, parent);
            index = parent;
        }
    }

    private void bubbleDown(int index) {
        int smallest = index;
        int left = leftChildIndex(index);
        int right = rightChildIndex(index);

        if (left < size && heapArray[left] < heapArray[smallest]) {
            smallest = left;
        }
        if (right < size && heapArray[right] < heapArray[smallest]) {
            smallest = right;
        }

        if (smallest != index) {
            swap(index, smallest);
            bubbleDown(smallest);
        }
    }

    private void updateKey(int index, int newVal) {
        int oldVal = heapArray[index];
        heapArray[index] = newVal;

        if (newVal < oldVal) {
            bubbleUp(index);
        } else if (newVal > oldVal) {
            bubbleDown(index);
        }
    }

    public boolean insertKey(int key) {
        if (isFull()) {
            return false;
        }

        heapArray[size] = key;
        bubbleUp(size);
        size++;
        return true;
    }

    public void decreaseKey(int key, int newVal) {
        validateIndex(key);
        updateKey(key, newVal);
    }

    public int getMin() {
        if (isEmpty()) {
            return Integer.MAX_VALUE;
        }
        return heapArray[0];
    }

    public int extractMin() {
        if (isEmpty()) {
            return Integer.MAX_VALUE;
        }

        if (size == 1) {
            size--;
            return heapArray[0];
        }

        int root = heapArray[0];
        heapArray[0] = heapArray[size - 1];
        size--;
        bubbleDown(0);
        return root;
    }

    public void deleteKey(int key) {
        validateIndex(key);
        heapArray[key] = Integer.MIN_VALUE;
        bubbleUp(key);
        extractMin();
    }

    public void increaseKey(int key, int newVal) {
        validateIndex(key);
        updateKey(key, newVal);
    }

    public void changeValueOnAKey(int key, int newVal) {
        validateIndex(key);
        if (heapArray[key] == newVal) {
            return;
        }
        updateKey(key, newVal);
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
