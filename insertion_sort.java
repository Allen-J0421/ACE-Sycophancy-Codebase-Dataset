import java.util.Arrays;
import java.util.Comparator;
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
 * cannot be instantiated. Three flavours are provided:
 * <ul>
 *   <li>{@link #sort(int[])} — fast primitive path, no boxing;</li>
 *   <li>{@link #sort(Object[])} — any {@link Comparable} type, natural order;</li>
 *   <li>{@link #sort(Object[], Comparator)} — any type with a custom order.</li>
 * </ul>
 */
public final class InsertionSort {

    private InsertionSort() {
        // Utility class; not meant to be instantiated.
    }

    /**
     * Sorts the given array of primitives in ascending order, in place.
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

    /**
     * Sorts the given array in ascending natural order, in place.
     *
     * @param arr the array to sort; must not be {@code null}
     * @param <T> the element type, which must be {@link Comparable}
     * @throws NullPointerException if {@code arr} is {@code null}
     */
    public static <T extends Comparable<? super T>> void sort(T[] arr) {
        sort(arr, Comparator.naturalOrder());
    }

    /**
     * Sorts the given array in place using the supplied comparator.
     *
     * @param arr        the array to sort; must not be {@code null}
     * @param comparator the ordering to impose; must not be {@code null}
     * @param <T>        the element type
     * @throws NullPointerException if {@code arr} or {@code comparator} is {@code null}
     */
    public static <T> void sort(T[] arr, Comparator<? super T> comparator) {
        Objects.requireNonNull(arr, "arr");
        Objects.requireNonNull(comparator, "comparator");
        for (int i = 1; i < arr.length; i++) {
            T key = arr[i];
            int j = i - 1;

            // Shift elements that should come after key one position to the right.
            while (j >= 0 && comparator.compare(arr[j], key) > 0) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;
        }
    }

    public static void main(String[] args) {
        int[] ints = { 12, 11, 13, 5, 6 };
        sort(ints);
        System.out.println(Arrays.toString(ints));

        String[] words = { "pear", "apple", "fig", "banana" };
        sort(words);
        System.out.println(Arrays.toString(words));

        Integer[] descending = { 12, 11, 13, 5, 6 };
        sort(descending, Comparator.reverseOrder());
        System.out.println(Arrays.toString(descending));
    }
}
