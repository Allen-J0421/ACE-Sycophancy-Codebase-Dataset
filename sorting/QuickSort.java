package sorting;

import java.util.Comparator;

/**
 * In-place quicksort using a median-of-three pivot and Lomuto partitioning.
 *
 * <p>The median-of-three pivot avoids the O(n²) degeneration that a naive
 * first/last-element pivot suffers on already-sorted input. Recursion descends
 * into the smaller partition and loops on the larger one, bounding stack depth
 * to O(log n). Average O(n log n); not stable. An observer sees both
 * comparisons and swaps.
 */
public final class QuickSort implements Sorter {

    @Override
    public <T> void sort(T[] array, Comparator<? super T> comparator, SortObserver observer) {
        SortSupport.requireArgs(array, comparator, observer);
        Comparator<? super T> cmp = SortSupport.counting(comparator, observer);
        sort(array, 0, array.length - 1, cmp, observer);
    }

    private static <T> void sort(T[] array, int lo, int hi,
                                 Comparator<? super T> cmp, SortObserver observer) {
        while (lo < hi) {
            int p = partition(array, lo, hi, cmp, observer);
            if (p - lo < hi - p) {
                sort(array, lo, p - 1, cmp, observer);
                lo = p + 1;
            } else {
                sort(array, p + 1, hi, cmp, observer);
                hi = p - 1;
            }
        }
    }

    private static <T> int partition(T[] array, int lo, int hi,
                                     Comparator<? super T> cmp, SortObserver observer) {
        int mid = lo + (hi - lo) / 2;
        // Order lo <= mid <= hi so array[mid] is the median of the three.
        if (cmp.compare(array[mid], array[lo]) < 0) {
            SortSupport.swap(array, lo, mid, observer);
        }
        if (cmp.compare(array[hi], array[lo]) < 0) {
            SortSupport.swap(array, lo, hi, observer);
        }
        if (cmp.compare(array[hi], array[mid]) < 0) {
            SortSupport.swap(array, mid, hi, observer);
        }
        // Move the median pivot to the end and partition around it.
        SortSupport.swap(array, mid, hi, observer);
        T pivot = array[hi];
        int i = lo;
        for (int j = lo; j < hi; j++) {
            if (cmp.compare(array[j], pivot) < 0) {
                SortSupport.swap(array, i, j, observer);
                i++;
            }
        }
        SortSupport.swap(array, i, hi, observer);
        return i;
    }
}
