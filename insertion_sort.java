import java.util.Arrays;
import java.util.Objects;

/**
 * In-place insertion sort.
 *
 * <p>Insertion sort builds the sorted array one element at a time: each new
 * element is compared against the already-sorted prefix to its left and shifted
 * into place. It runs in O(n^2) time in the worst case and O(n) on
 * already-sorted input, and sorts in place using O(1) extra space.
 *
 * <p>This is a stateless utility class, so all methods are {@code static} and it
 * cannot be instantiated.
 */
public final class InsertionSort {

    private InsertionSort() {
        // Utility class; not meant to be instantiated.
    }

    /**
     * Sorts the given array in ascending order, in place.
     *
     * @param arr the array to sort; must not be {@code null}
     * @throws NullPointerException if {@code arr} is {@code null}
     */
    public static void sort(int[] arr) {
        Objects.requireNonNull(arr, "arr");
        for (int i = 1; i < arr.length; i++) {
            int key = arr[i];
            int j = i - 1;

            // Shift elements greater than key one position to the right.
            while (j >= 0 && arr[j] > key) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;
        }
    }

    public static void main(String[] args) {
        int[] arr = { 12, 11, 13, 5, 6 };
        sort(arr);
        System.out.println(Arrays.toString(arr));
    }
}
