package sorting;

import java.util.Comparator;
import java.util.Objects;

/**
 * Bubble sort with the classic early-exit optimization: a pass that completes
 * without swapping any adjacent pair means the array is already ordered, so the
 * algorithm stops early. Worst case is O(n²) comparisons; a sorted (or nearly
 * sorted) input runs in O(n).
 *
 * <p>The class offers symmetric object and primitive entry points:
 * <ul>
 *   <li>the generic {@link #sort(Object[], Comparator)} required by
 *       {@link Sorter}, for object arrays; and</li>
 *   <li>{@link #sort(int[], IntComparator)} for primitive {@code int} arrays,
 *       with a {@link #sort(int[])} convenience for ascending order. These
 *       preserve the original allocation-free {@code int[]} behavior.</li>
 * </ul>
 */
public final class BubbleSort implements Sorter {

    @Override
    public <T> void sort(T[] array, Comparator<? super T> comparator) {
        Objects.requireNonNull(array, "array");
        Objects.requireNonNull(comparator, "comparator");
        int n = array.length;
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                if (comparator.compare(array[j], array[j + 1]) > 0) {
                    SortSupport.swap(array, j, j + 1);
                    swapped = true;
                }
            }
            if (!swapped) {
                break;
            }
        }
    }

    /**
     * Sorts a primitive {@code int} array in place in ascending order.
     *
     * @param array the array to sort; must not be {@code null}
     */
    public static void sort(int[] array) {
        sort(array, IntComparator.ASCENDING);
    }

    /**
     * Sorts a primitive {@code int} array in place using {@code comparator},
     * with no boxing.
     *
     * @param array      the array to sort; must not be {@code null}
     * @param comparator the ordering to impose; must not be {@code null}
     */
    public static void sort(int[] array, IntComparator comparator) {
        Objects.requireNonNull(array, "array");
        Objects.requireNonNull(comparator, "comparator");
        int n = array.length;
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                if (comparator.compare(array[j], array[j + 1]) > 0) {
                    SortSupport.swap(array, j, j + 1);
                    swapped = true;
                }
            }
            if (!swapped) {
                break;
            }
        }
    }

}
