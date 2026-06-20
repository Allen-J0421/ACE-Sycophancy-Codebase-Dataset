import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

/**
 * In-place heap sort.
 *
 * <p>The array is first arranged into a max-heap, then the maximum element is
 * repeatedly swapped to the end of the unsorted region and the heap is restored,
 * yielding an ascending sort in O(n log n) time and O(1) extra space.
 *
 * <p>Works on any reference type via a {@link Comparator}, with a convenience
 * overload for {@link Comparable} elements.
 */
final class HeapSort {

    private HeapSort() {
        // Utility class: no instances.
    }

    /**
     * Sorts {@code arr} into ascending natural order, in place.
     *
     * @param arr the array to sort; may be empty
     * @throws NullPointerException if {@code arr} is null
     */
    public static <T extends Comparable<? super T>> void sort(T[] arr) {
        sort(arr, Comparator.naturalOrder());
    }

    /**
     * Sorts {@code arr} in place using {@code comparator} to define order.
     *
     * @param arr        the array to sort; may be empty
     * @param comparator the ordering to impose
     * @throws NullPointerException if {@code arr} or {@code comparator} is null
     */
    public static <T> void sort(T[] arr, Comparator<? super T> comparator) {
        Objects.requireNonNull(arr, "arr");
        Objects.requireNonNull(comparator, "comparator");

        int n = arr.length;

        // Build a max-heap from the bottom up.
        for (int i = n / 2 - 1; i >= 0; i--) {
            siftDown(arr, n, i, comparator);
        }

        // Repeatedly move the current maximum (root) to the end and shrink the heap.
        for (int end = n - 1; end > 0; end--) {
            swap(arr, 0, end);
            siftDown(arr, end, 0, comparator);
        }
    }

    /**
     * Restores the max-heap property for the subtree rooted at {@code i},
     * considering only the first {@code heapSize} elements as part of the heap.
     */
    private static <T> void siftDown(T[] arr, int heapSize, int i, Comparator<? super T> cmp) {
        while (true) {
            int largest = i;
            int left = 2 * i + 1;
            int right = 2 * i + 2;

            if (left < heapSize && cmp.compare(arr[left], arr[largest]) > 0) {
                largest = left;
            }
            if (right < heapSize && cmp.compare(arr[right], arr[largest]) > 0) {
                largest = right;
            }
            if (largest == i) {
                return;
            }

            swap(arr, i, largest);
            i = largest;
        }
    }

    private static <T> void swap(T[] arr, int a, int b) {
        T temp = arr[a];
        arr[a] = arr[b];
        arr[b] = temp;
    }

    public static void main(String[] args) {
        Integer[] arr = { 9, 4, 3, 8, 10, 2, 5 };
        sort(arr);
        System.out.println(Arrays.toString(arr));
    }
}
