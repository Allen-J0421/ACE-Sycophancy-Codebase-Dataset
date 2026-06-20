package sorting;

import java.util.Comparator;

/**
 * Insertion sort: grows a sorted prefix one element at a time, shifting larger
 * elements to the right to open a slot for each new value.
 *
 * <p>O(n²) comparisons in the worst case but O(n) on already- or nearly-sorted
 * input. Stable and in place, with no auxiliary allocation. It shifts rather
 * than swaps, so an observer sees comparisons but no swaps.
 */
public final class InsertionSort implements Sorter {

    @Override
    public <T> void sort(T[] array, Comparator<? super T> comparator, SortObserver observer) {
        SortSupport.requireArgs(array, comparator, observer);
        Comparator<? super T> cmp = SortSupport.counting(comparator, observer);
        for (int i = 1; i < array.length; i++) {
            T key = array[i];
            int j = i - 1;
            while (j >= 0 && cmp.compare(array[j], key) > 0) {
                array[j + 1] = array[j];
                j--;
            }
            array[j + 1] = key;
        }
    }
}
