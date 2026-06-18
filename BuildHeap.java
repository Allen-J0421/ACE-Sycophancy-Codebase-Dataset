public final class BuildHeap {

    private BuildHeap() {
        // Utility class.
    }

    public static void heapify(int[] arr, int heapSize, int rootIndex) {
        int largest = rootIndex;
        int leftChild = 2 * rootIndex + 1;
        int rightChild = 2 * rootIndex + 2;

        if (leftChild < heapSize && arr[leftChild] > arr[largest]) {
            largest = leftChild;
        }

        if (rightChild < heapSize && arr[rightChild] > arr[largest]) {
            largest = rightChild;
        }

        if (largest != rootIndex) {
            swap(arr, rootIndex, largest);
            heapify(arr, heapSize, largest);
        }
    }

    public static void buildHeap(int[] arr) {
        if (arr == null || arr.length < 2) {
            return;
        }

        for (int i = (arr.length / 2) - 1; i >= 0; i--) {
            heapify(arr, arr.length, i);
        }
    }

    public static void main(String[] args) {
        int[] arr = {1, 3, 5, 4, 6, 13, 10, 9, 8, 15, 17};

        buildHeap(arr);

        for (int value : arr) {
            System.out.print(value + " ");
        }
        System.out.println();
    }

    private static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
}
