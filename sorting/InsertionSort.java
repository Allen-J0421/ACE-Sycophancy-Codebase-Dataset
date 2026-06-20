package sorting;

import java.util.Comparator;
import java.util.Objects;

/**
 * Insertion sort: grows a sorted prefix one element at a time, shifting larger
 * elements to the right to open a slot for each new value.
 *
 * <p>O(n²) comparisons in the worst case but O(n) on already- or nearly-sorted
 * input. Stable and in place, with no auxiliary allocation.
 */
public final class InsertionSort implements Sorter {

    @Override
    public <T> void sort(T[] array, Comparator<? super T> comparator) {
        Objects.requireNonNull(array, "array");
        Objects.requireNonNull(comparator, "comparator");
        for (int i = 1; i < array.length; i++) {
            T key = array[i];
            int j = i - 1;
            while (j >= 0 && comparator.compare(array[j], key) > 0) {
                array[j + 1] = array[j];
                j--;
            }
            array[j + 1] = key;
        }
    }
}
