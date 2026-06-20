package sorting;

import java.util.Comparator;

/**
 * Top-down merge sort: recursively splits the array in half, sorts each half,
 * then merges the two sorted runs.
 *
 * <p>O(n log n) comparisons in all cases (best, average, and worst) and stable,
 * at the cost of an O(n) auxiliary buffer. It copies into that buffer rather
 * than swapping, so an observer sees comparisons but no swaps.
 */
public final class MergeSort implements Sorter {

    @Override
    public <T> void sort(T[] array, Comparator<? super T> comparator, SortObserver observer) {
        SortSupport.requireArgs(array, comparator, observer);
        if (array.length < 2) {
            return;
        }
        Comparator<? super T> cmp = SortSupport.counting(comparator, observer);
        Object[] buffer = new Object[array.length];
        sort(array, buffer, 0, array.length - 1, cmp);
    }

    private static <T> void sort(T[] array, Object[] buffer, int lo, int hi,
                                 Comparator<? super T> comparator) {
        if (lo >= hi) {
            return;
        }
        int mid = lo + (hi - lo) / 2;
        sort(array, buffer, lo, mid, comparator);
        sort(array, buffer, mid + 1, hi, comparator);
        merge(array, buffer, lo, mid, hi, comparator);
    }

    @SuppressWarnings("unchecked")
    private static <T> void merge(T[] array, Object[] buffer, int lo, int mid, int hi,
                                  Comparator<? super T> comparator) {
        for (int k = lo; k <= hi; k++) {
            buffer[k] = array[k];
        }
        int i = lo;
        int j = mid + 1;
        for (int k = lo; k <= hi; k++) {
            if (i > mid) {
                array[k] = (T) buffer[j++];
            } else if (j > hi) {
                array[k] = (T) buffer[i++];
            } else if (comparator.compare((T) buffer[j], (T) buffer[i]) < 0) {
                // Take the right run only when it is strictly smaller, so equal
                // elements keep their original left-to-right order (stable).
                array[k] = (T) buffer[j++];
            } else {
                array[k] = (T) buffer[i++];
            }
        }
    }
}
