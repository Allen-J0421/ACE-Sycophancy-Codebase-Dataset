import java.util.Comparator;
import java.util.Objects;

/**
 * A robust, reusable Quicksort implementation.
 *
 * <p>Beyond the textbook algorithm, this implementation hardens the three
 * places where a naive quicksort goes wrong in practice:
 *
 * <ul>
 *   <li><b>Bounded stack usage.</b> Recursion always descends into the
 *       <em>smaller</em> partition and loops on the larger one. This caps the
 *       recursion depth at O(log n) and prevents the {@link StackOverflowError}
 *       a textbook recursive quicksort throws on large sorted input.</li>
 *   <li><b>Resistance to quadratic blow-up.</b> The pivot is chosen by
 *       median-of-three sampling, so already-sorted and reverse-sorted inputs
 *       — the classic worst cases for a first/last-element pivot — still run in
 *       O(n log n).</li>
 *   <li><b>Speed on small ranges.</b> Sub-arrays at or below a threshold are
 *       finished with insertion sort, which beats quicksort's bookkeeping
 *       overhead there.</li>
 * </ul>
 *
 * <p>This is a stateless utility class: every entry point is static and operates
 * only on its argument array, so concurrent calls on distinct arrays are safe.
 */
public final class QuickSort {

    /** Sub-ranges of at most this length are sorted with insertion sort. */
    private static final int INSERTION_SORT_THRESHOLD = 16;

    private QuickSort() {
        // Utility class — not instantiable.
    }

    // ------------------------------------------------------------------
    // Primitive int[] API (optimized, no boxing)
    // ------------------------------------------------------------------

    /**
     * Sorts the given array of ints into ascending order, in place.
     *
     * @param array the array to sort; must not be {@code null}
     * @throws NullPointerException if {@code array} is {@code null}
     */
    public static void sort(int[] array) {
        Objects.requireNonNull(array, "array must not be null");
        sortRange(array, 0, array.length - 1);
    }

    private static void sortRange(int[] a, int low, int high) {
        while (low < high) {
            if (high - low < INSERTION_SORT_THRESHOLD) {
                insertionSort(a, low, high);
                return;
            }
            int p = partition(a, low, high);
            // Recurse into the smaller side, then loop on the larger side.
            // This bounds the live recursion depth to O(log n).
            if (p - low < high - p) {
                sortRange(a, low, p - 1);
                low = p + 1;
            } else {
                sortRange(a, p + 1, high);
                high = p - 1;
            }
        }
    }

    /**
     * Median-of-three partition (Sedgewick scheme). Orders {@code a[low]},
     * {@code a[mid]} and {@code a[high]}, parks the median pivot at
     * {@code high - 1} as a sentinel, then partitions {@code [low, high]} around
     * it. Requires {@code high - low >= 2}, which the threshold guarantees.
     *
     * @return the final resting index of the pivot
     */
    private static int partition(int[] a, int low, int high) {
        int mid = low + (high - low) / 2;
        if (a[mid] < a[low]) swap(a, low, mid);
        if (a[high] < a[low]) swap(a, low, high);
        if (a[high] < a[mid]) swap(a, mid, high);
        // Now a[low] <= a[mid] <= a[high]. Stash the pivot just before high;
        // a[low] and a[high] act as sentinels for the inner scans.
        swap(a, mid, high - 1);
        int pivot = a[high - 1];

        int i = low;
        int j = high - 1;
        while (true) {
            while (a[++i] < pivot) { /* advance */ }
            while (a[--j] > pivot) { /* advance */ }
            if (i >= j) {
                break;
            }
            swap(a, i, j);
        }
        swap(a, i, high - 1); // restore the pivot to its sorted position
        return i;
    }

    private static void insertionSort(int[] a, int low, int high) {
        for (int i = low + 1; i <= high; i++) {
            int key = a[i];
            int j = i - 1;
            while (j >= low && a[j] > key) {
                a[j + 1] = a[j];
                j--;
            }
            a[j + 1] = key;
        }
    }

    private static void swap(int[] a, int i, int j) {
        int temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }

    // ------------------------------------------------------------------
    // Generic object API
    // ------------------------------------------------------------------

    /**
     * Sorts the given array into ascending order according to the elements'
     * {@linkplain Comparable natural ordering}, in place.
     *
     * @param array the array to sort; must not be {@code null}
     * @param <T>   a comparable element type
     * @throws NullPointerException if {@code array} is {@code null}
     */
    public static <T extends Comparable<? super T>> void sort(T[] array) {
        sort(array, Comparator.naturalOrder());
    }

    /**
     * Sorts the given array in place using the supplied comparator.
     *
     * @param array      the array to sort; must not be {@code null}
     * @param comparator the ordering to impose; must not be {@code null}
     * @param <T>        the element type
     * @throws NullPointerException if {@code array} or {@code comparator} is {@code null}
     */
    public static <T> void sort(T[] array, Comparator<? super T> comparator) {
        Objects.requireNonNull(array, "array must not be null");
        Objects.requireNonNull(comparator, "comparator must not be null");
        sortRange(array, 0, array.length - 1, comparator);
    }

    private static <T> void sortRange(T[] a, int low, int high, Comparator<? super T> cmp) {
        while (low < high) {
            if (high - low < INSERTION_SORT_THRESHOLD) {
                insertionSort(a, low, high, cmp);
                return;
            }
            int p = partition(a, low, high, cmp);
            if (p - low < high - p) {
                sortRange(a, low, p - 1, cmp);
                low = p + 1;
            } else {
                sortRange(a, p + 1, high, cmp);
                high = p - 1;
            }
        }
    }

    private static <T> int partition(T[] a, int low, int high, Comparator<? super T> cmp) {
        int mid = low + (high - low) / 2;
        if (cmp.compare(a[mid], a[low]) < 0) swap(a, low, mid);
        if (cmp.compare(a[high], a[low]) < 0) swap(a, low, high);
        if (cmp.compare(a[high], a[mid]) < 0) swap(a, mid, high);
        swap(a, mid, high - 1);
        T pivot = a[high - 1];

        int i = low;
        int j = high - 1;
        while (true) {
            while (cmp.compare(a[++i], pivot) < 0) { /* advance */ }
            while (cmp.compare(a[--j], pivot) > 0) { /* advance */ }
            if (i >= j) {
                break;
            }
            swap(a, i, j);
        }
        swap(a, i, high - 1);
        return i;
    }

    private static <T> void insertionSort(T[] a, int low, int high, Comparator<? super T> cmp) {
        for (int i = low + 1; i <= high; i++) {
            T key = a[i];
            int j = i - 1;
            while (j >= low && cmp.compare(a[j], key) > 0) {
                a[j + 1] = a[j];
                j--;
            }
            a[j + 1] = key;
        }
    }

    private static <T> void swap(T[] a, int i, int j) {
        T temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }
}
