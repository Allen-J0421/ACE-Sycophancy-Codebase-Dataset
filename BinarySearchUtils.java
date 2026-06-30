import java.util.Objects;

/**
 * Utility methods for searching sorted arrays.
 */
public final class BinarySearchUtils {
    private BinarySearchUtils() {
    }

    /**
     * Searches for {@code target} in {@code array} using binary search.
     *
     * <p>The array must already be sorted in ascending order according to each
     * element's natural ordering, as defined by {@link Comparable#compareTo(Object)}.
     * This method validates that precondition before searching and throws an
     * {@link IllegalArgumentException} when the array is not sorted.</p>
     *
     * <p>The generic type {@code T} must implement {@link Comparable} so that
     * elements can be ordered relative to the target. The {@code ? super T}
     * bound allows a type to be compared by a comparable supertype.</p>
     *
     * <p>The binary search phase runs in {@code O(log n)} time. Because this
     * implementation first validates sorted order with a linear scan, the full
     * method call runs in {@code O(n)} time.</p>
     *
     * @param array the sorted array to search
     * @param target the value to find
     * @param <T> the element type, ordered by its natural {@link Comparable} implementation
     * @return the index of {@code target}, or {@code -1} when the target is absent
     * @throws NullPointerException if {@code array}, {@code target}, or any array element is {@code null}
     * @throws IllegalArgumentException if {@code array} is not sorted in ascending order
     */
    public static <T extends Comparable<? super T>> int binarySearch(T[] array, T target) {
        Objects.requireNonNull(array, "array must not be null");
        Objects.requireNonNull(target, "target must not be null");
        validateSorted(array);

        int low = 0;
        int high = array.length - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            int comparison = array[mid].compareTo(target);

            if (comparison == 0) {
                return mid;
            }

            if (comparison < 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return -1;
    }

    private static <T extends Comparable<? super T>> void validateSorted(T[] array) {
        for (int index = 0; index < array.length; index++) {
            Objects.requireNonNull(array[index], "array elements must not be null");

            if (index > 0 && array[index - 1].compareTo(array[index]) > 0) {
                throw new IllegalArgumentException(
                        "array must be sorted in ascending order; values at indexes "
                                + (index - 1) + " and " + index + " are out of order");
            }
        }
    }
}
