package sorting;

import java.util.Comparator;

/**
 * Strategy abstraction for an in-place array sorting algorithm.
 *
 * <p>Implementations sort {@code array} in place according to the supplied
 * {@link Comparator}. Keeping this as a single-method (functional) interface
 * lets callers swap algorithms — or pass a lambda — without depending on a
 * concrete implementation.
 */
@FunctionalInterface
public interface Sorter {

    /**
     * Sorts {@code array} in place in ascending order as defined by
     * {@code comparator}.
     *
     * @param array      the array to sort; must not be {@code null}
     * @param comparator the ordering to impose; must not be {@code null}
     * @param <T>        the element type
     */
    <T> void sort(T[] array, Comparator<? super T> comparator);

    /**
     * Sorts {@code array} in place using the elements' natural ordering.
     *
     * @param array the array to sort; must not be {@code null}
     * @param <T>   a {@link Comparable} element type
     */
    default <T extends Comparable<? super T>> void sort(T[] array) {
        sort(array, Comparator.naturalOrder());
    }
}
