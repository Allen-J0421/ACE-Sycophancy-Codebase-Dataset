package sorting;

import java.util.Comparator;
import java.util.Objects;

/**
 * In-place quicksort using a median-of-three pivot and Lomuto partitioning.
 *
 * <p>The median-of-three pivot avoids the O(n²) degeneration that a naive
 * first/last-element pivot suffers on already-sorted input. Recursion descends
 * into the smaller partition and loops on the larger one, bounding stack depth
 * to O(log n). Average O(n log n); not stable.
 */
public final class QuickSort implements Sorter {

    @Override
    public <T> void sort(T[] array, Comparator<? super T> comparator) {
        Objects.requireNonNull(array, "array");
        Objects.requireNonNull(comparator, "comparator");
        sort(array, 0, array.length - 1, comparator);
    }

    private static <T> void sort(T[] array, int lo, int hi, Comparator<? super T> comparator) {
        while (lo < hi) {
            int p = partition(array, lo, hi, comparator);
            if (p - lo < hi - p) {
                sort(array, lo, p - 1, comparator);
                lo = p + 1;
            } else {
                sort(array, p + 1, hi, comparator);
                hi = p - 1;
            }
        }
    }

    private static <T> int partition(T[] array, int lo, int hi, Comparator<? super T> comparator) {
        int mid = lo + (hi - lo) / 2;
        // Order lo <= mid <= hi so array[mid] is the median of the three.
        if (comparator.compare(array[mid], array[lo]) < 0) {
            SortSupport.swap(array, lo, mid);
        }
        if (comparator.compare(array[hi], array[lo]) < 0) {
            SortSupport.swap(array, lo, hi);
        }
        if (comparator.compare(array[hi], array[mid]) < 0) {
            SortSupport.swap(array, mid, hi);
        }
        // Move the median pivot to the end and partition around it.
        SortSupport.swap(array, mid, hi);
        T pivot = array[hi];
        int i = lo;
        for (int j = lo; j < hi; j++) {
            if (comparator.compare(array[j], pivot) < 0) {
                SortSupport.swap(array, i, j);
                i++;
            }
        }
        SortSupport.swap(array, i, hi);
        return i;
    }
}
