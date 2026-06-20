package sorting;

import java.util.Comparator;

/**
 * Strategy abstraction for an in-place array sorting algorithm.
 *
 * <p>Implementations sort {@code array} in place according to the supplied
 * {@link Comparator}, notifying a {@link SortObserver} of each comparison and
 * swap. The two- and one-argument overloads are conveniences that observe
 * nothing. Keeping this as a single-method (functional) interface lets callers
 * swap algorithms — or pass a lambda — without depending on a concrete
 * implementation.
 */
@FunctionalInterface
public interface Sorter {

    /**
     * Sorts {@code array} in place in ascending order as defined by
     * {@code comparator}, reporting each comparison and swap to
     * {@code observer}.
     *
     * @param array      the array to sort; must not be {@code null}
     * @param comparator the ordering to impose; must not be {@code null}
     * @param observer   the hook to notify; use {@link SortObserver#NO_OP} for
     *                   none; must not be {@code null}
     * @param <T>        the element type
     */
    <T> void sort(T[] array, Comparator<? super T> comparator, SortObserver observer);

    /**
     * Sorts {@code array} in place using {@code comparator}, observing nothing.
     *
     * @param array      the array to sort; must not be {@code null}
     * @param comparator the ordering to impose; must not be {@code null}
     * @param <T>        the element type
     */
    default <T> void sort(T[] array, Comparator<? super T> comparator) {
        sort(array, comparator, SortObserver.NO_OP);
    }

    /**
     * Sorts {@code array} in place using the elements' natural ordering,
     * observing nothing.
     *
     * @param array the array to sort; must not be {@code null}
     * @param <T>   a {@link Comparable} element type
     */
    default <T extends Comparable<? super T>> void sort(T[] array) {
        sort(array, Comparator.naturalOrder(), SortObserver.NO_OP);
    }
}
