package sorting;

import java.util.Comparator;
import java.util.Objects;

/**
 * Internal helpers shared by {@link Sorter} implementations. Package-private:
 * not part of the public API.
 */
final class SortSupport {

    private SortSupport() {
        // Utility class; not instantiable.
    }

    /** Validates the common (array, comparator) arguments of a sort call. */
    static void requireArgs(Object[] array, Comparator<?> comparator) {
        Objects.requireNonNull(array, "array");
        Objects.requireNonNull(comparator, "comparator");
    }

    /** Validates the arguments of a primitive {@code int[]} sort call. */
    static void requireArgs(int[] array, IntComparator comparator) {
        Objects.requireNonNull(array, "array");
        Objects.requireNonNull(comparator, "comparator");
    }

    /** Swaps the elements at indices {@code i} and {@code j} of an object array. */
    static <T> void swap(T[] array, int i, int j) {
        T temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    /** Swaps the elements at indices {@code i} and {@code j} of an int array. */
    static void swap(int[] array, int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
}
