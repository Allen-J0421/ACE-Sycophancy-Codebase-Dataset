import java.util.Arrays;
import java.util.Objects;

/**
 * In-place heap sort over an array of ints.
 *
 * <p>The array is first arranged into a max-heap, then the maximum element is
 * repeatedly swapped to the end of the unsorted region and the heap is restored,
 * yielding an ascending sort in O(n log n) time and O(1) extra space.
 */
final class HeapSort {

    private HeapSort() {
        // Utility class: no instances.
    }

    /**
     * Sorts {@code arr} in ascending order, in place.
     *
     * @param arr the array to sort; may be empty
     * @throws NullPointerException if {@code arr} is null
     */
    public static void sort(int[] arr) {
        Objects.requireNonNull(arr, "arr");

        int n = arr.length;

        // Build a max-heap from the bottom up.
        for (int i = n / 2 - 1; i >= 0; i--) {
            siftDown(arr, n, i);
        }

        // Repeatedly move the current maximum (root) to the end and shrink the heap.
        for (int end = n - 1; end > 0; end--) {
            swap(arr, 0, end);
            siftDown(arr, end, 0);
        }
    }

    /**
     * Restores the max-heap property for the subtree rooted at {@code i},
     * considering only the first {@code heapSize} elements as part of the heap.
     */
    private static void siftDown(int[] arr, int heapSize, int i) {
        while (true) {
            int largest = i;
            int left = 2 * i + 1;
            int right = 2 * i + 2;

            if (left < heapSize && arr[left] > arr[largest]) {
                largest = left;
            }
            if (right < heapSize && arr[right] > arr[largest]) {
                largest = right;
            }
            if (largest == i) {
                return;
            }

            swap(arr, i, largest);
            i = largest;
        }
    }

    private static void swap(int[] arr, int a, int b) {
        int temp = arr[a];
        arr[a] = arr[b];
        arr[b] = temp;
    }

    public static void main(String[] args) {
        int[] arr = { 9, 4, 3, 8, 10, 2, 5 };
        sort(arr);
        System.out.println(Arrays.toString(arr));
    }
}
