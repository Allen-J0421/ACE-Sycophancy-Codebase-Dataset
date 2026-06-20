import java.util.Comparator;

/**
 * An in-place array sorting algorithm.
 *
 * <p>Decouples the choice of sorting algorithm from callers: any implementation
 * (insertion sort, merge sort, ...) can be supplied wherever a {@code Sorter} is
 * expected. Implementations sort the given array in place.
 */
public interface Sorter {

    /**
     * Sorts an array of primitives in ascending order, in place.
     *
     * @param arr the array to sort; must not be {@code null}
     * @throws NullPointerException if {@code arr} is {@code null}
     */
    void sort(int[] arr);

    /**
     * Sorts an array in place using the supplied comparator.
     *
     * @param arr        the array to sort; must not be {@code null}
     * @param comparator the ordering to impose; must not be {@code null}
     * @param <T>        the element type
     * @throws NullPointerException if {@code arr} or {@code comparator} is {@code null}
     */
    <T> void sort(T[] arr, Comparator<? super T> comparator);

    /**
     * Sorts an array in ascending natural order, in place.
     *
     * @param arr the array to sort; must not be {@code null}
     * @param <T> the element type, which must be {@link Comparable}
     * @throws NullPointerException if {@code arr} is {@code null}
     */
    default <T extends Comparable<? super T>> void sort(T[] arr) {
        sort(arr, Comparator.naturalOrder());
    }
}
