package sorting;

import java.util.Comparator;
import java.util.Objects;

/**
 * Bubble sort with the classic early-exit optimization: a pass that completes
 * without swapping any adjacent pair means the array is already ordered, so the
 * algorithm stops early. Worst case is O(n²) comparisons; a sorted (or nearly
 * sorted) input runs in O(n).
 *
 * <p>This class offers two entry points:
 * <ul>
 *   <li>the generic {@link #sort(Object[], Comparator)} required by
 *       {@link Sorter}, for object arrays; and</li>
 *   <li>a primitive {@link #sort(int[])} overload that preserves the original
 *       allocation-free {@code int[]} behavior.</li>
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
                    T temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
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
     * <p>This mirrors the behavior of the original {@code bubbleSort} routine
     * while deriving the length from the array itself rather than a separate
     * {@code n} parameter.
     *
     * @param array the array to sort; must not be {@code null}
     */
    public static void sort(int[] array) {
        Objects.requireNonNull(array, "array");
        int n = array.length;
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                if (array[j] > array[j + 1]) {
                    int temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                    swapped = true;
                }
            }
            if (!swapped) {
                break;
            }
        }
    }
}
