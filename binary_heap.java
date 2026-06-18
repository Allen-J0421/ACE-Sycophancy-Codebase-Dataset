class MinHeap {

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
        if (size == heapArray.length) {
            return false;
        }

        int insertIndex = size;
        heapArray[insertIndex] = key;
        size++;

        bubbleUp(insertIndex);
        return true;
    }

    public void decreaseKey(int key, int newValue) {
        heapArray[key] = newValue;
        bubbleUp(key);
    }

    public int getMin() {
        return heapArray[0];
    }

    public int extractMin() {
        if (size <= 0) {
            return Integer.MAX_VALUE;
        }

        if (size == 1) {
            size--;
            return heapArray[0];
        }

        int root = heapArray[0];

        heapArray[0] = heapArray[size - 1];
        size--;
        minHeapify(0);

        return root;
    }

    public void deleteKey(int key) {
        decreaseKey(key, Integer.MIN_VALUE);
        extractMin();
    }

    private void minHeapify(int key) {
        int left = leftChildIndex(key);
        int right = rightChildIndex(key);

        int smallest = key;
        if (left < size && heapArray[left] < heapArray[smallest]) {
            smallest = left;
        }
        if (right < size && heapArray[right] < heapArray[smallest]) {
            smallest = right;
        }

        if (smallest != key) {
            swap(key, smallest);
            minHeapify(smallest);
        }
    }

    public void increaseKey(int key, int newValue) {
        heapArray[key] = newValue;
        minHeapify(key);
    }

    public void changeValueOnAKey(int key, int newValue) {
        if (heapArray[key] == newValue) {
            return;
        }
        if (heapArray[key] < newValue) {
            increaseKey(key, newValue);
        } else {
            decreaseKey(key, newValue);
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
