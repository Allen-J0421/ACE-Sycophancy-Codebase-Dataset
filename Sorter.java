/**
 * Strategy for sorting an array of {@code T} into ascending order, in place.
 *
 * @param <T> the element type
 */
public interface Sorter<T> {

    /**
     * Sorts the given array into ascending order, modifying it in place.
     *
     * @param array the array to sort; a {@code null} or single-element array is
     *              already sorted and left untouched.
     */
    void sort(T[] array);
}
