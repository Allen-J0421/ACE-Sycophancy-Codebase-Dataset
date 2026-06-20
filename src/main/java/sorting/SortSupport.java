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

    /** Validates the common (array, comparator, observer) arguments of a sort call. */
    static void requireArgs(Object[] array, Comparator<?> comparator, SortObserver observer) {
        Objects.requireNonNull(array, "array");
        Objects.requireNonNull(comparator, "comparator");
        Objects.requireNonNull(observer, "observer");
    }

    /** Validates the arguments of a primitive {@code int[]} sort call. */
    static void requireArgs(int[] array, IntComparator comparator) {
        Objects.requireNonNull(array, "array");
        Objects.requireNonNull(comparator, "comparator");
    }

    /**
     * Wraps {@code comparator} so that every comparison notifies {@code observer}.
     * Returns the original comparator unchanged for {@link SortObserver#NO_OP},
     * keeping the uninstrumented path free of overhead.
     */
    static <T> Comparator<? super T> counting(Comparator<? super T> comparator, SortObserver observer) {
        if (observer == SortObserver.NO_OP) {
            return comparator;
        }
        Comparator<T> counting = (a, b) -> {
            observer.onCompare();
            return comparator.compare(a, b);
        };
        return counting;
    }

    /**
     * Swaps the elements at indices {@code i} and {@code j} of an object array
     * and notifies {@code observer}.
     */
    static <T> void swap(T[] array, int i, int j, SortObserver observer) {
        T temp = array[i];
        array[i] = array[j];
        array[j] = temp;
        observer.onSwap();
    }

    /** Swaps the elements at indices {@code i} and {@code j} of an int array. */
    static void swap(int[] array, int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
}
