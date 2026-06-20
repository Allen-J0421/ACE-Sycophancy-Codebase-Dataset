import java.util.Arrays;

public class HeapSort {

    static void heapify(int[] arr, int heapSize, int rootIndex) {
        int currentIndex = rootIndex;

        while (true) {
            int largestIndex = currentIndex;
            int leftChildIndex = 2 * currentIndex + 1;
            int rightChildIndex = 2 * currentIndex + 2;

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

    static void heapSort(int[] arr) {
        int length = arr.length;

        buildMaxHeap(arr, length);

        for (int end = length - 1; end > 0; end--) {
            swap(arr, 0, end);
            heapify(arr, end, 0);
        }
    }

    static void buildMaxHeap(int[] arr, int length) {
        for (int parentIndex = length / 2 - 1; parentIndex >= 0; parentIndex--) {
            heapify(arr, length, parentIndex);
        }
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

        heapSort(arr);

        System.out.println(Arrays.toString(arr));
    }
}
