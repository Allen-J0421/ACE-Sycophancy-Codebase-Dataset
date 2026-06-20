import java.util.Arrays;

public final class HeapSort {

    private HeapSort() {
    }

    public static void sort(int[] values) {
        int length = values.length;

        buildMaxHeap(values, length);
        extractElementsFromHeap(values, length);
    }

    static void siftDown(int[] arr, int heapSize, int rootIndex) {
        int currentIndex = rootIndex;

        while (true) {
            int largestIndex = currentIndex;
            int leftChildIndex = leftChildIndex(currentIndex);
            int rightChildIndex = rightChildIndex(currentIndex);

            if (leftChildIndex < heapSize && arr[leftChildIndex] > arr[largestIndex]) {
                largestIndex = leftChildIndex;
            }

            if (rightChildIndex < heapSize && arr[rightChildIndex] > arr[largestIndex]) {
                largestIndex = rightChildIndex;
            }

            if (largestIndex == currentIndex) {
                return;
            }

            swap(arr, currentIndex, largestIndex);
            currentIndex = largestIndex;
        }
    }

    static void extractElementsFromHeap(int[] arr, int length) {
        for (int end = length - 1; end > 0; end--) {
            swap(arr, 0, end);
            siftDown(arr, end, 0);
        }
    }

    static void buildMaxHeap(int[] arr, int length) {
        for (int parentIndex = length / 2 - 1; parentIndex >= 0; parentIndex--) {
            siftDown(arr, length, parentIndex);
        }
    }

    static int leftChildIndex(int parentIndex) {
        return 2 * parentIndex + 1;
    }

    static int rightChildIndex(int parentIndex) {
        return 2 * parentIndex + 2;
    }

    static void swap(int[] arr, int firstIndex, int secondIndex) {
        if (firstIndex == secondIndex) {
            return;
        }

        int temp = arr[firstIndex];
        arr[firstIndex] = arr[secondIndex];
        arr[secondIndex] = temp;
    }

    public static void main(String[] args) {
        int[] arr = { 9, 4, 3, 8, 10, 2, 5 };

        sort(arr);

        System.out.println(Arrays.toString(arr));
    }
}
