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
     * element's natural ordering, as defined by {@link Comparable#compareTo(Object)}.</p>
     *
     * <p>This fast-path method does not validate sorted order. The caller is
     * responsible for ensuring the array is sorted before calling this method.
     * Use {@link #isSorted(Comparable[])} before calling this method, or use
     * {@link #binarySearch(Comparable[], Comparable, boolean)} with validation
     * enabled, when input safety is more important than avoiding the validation
     * scan.</p>
     *
     * <p>The generic type {@code T} must implement {@link Comparable} so that
     * elements can be ordered relative to the target. The {@code ? super T}
     * bound allows a type to be compared by a comparable supertype.</p>
     *
     * <p>This method runs in {@code O(log n)} time when the input array is
     * sorted.</p>
     *
     * @param array the sorted array to search
     * @param target the value to find
     * @param <T> the element type, ordered by its natural {@link Comparable} implementation
     * @return the index of {@code target}, or {@code -1} when the target is absent
     * @throws NullPointerException if {@code array}, {@code target}, or any array element is {@code null}
     */
    public static <T extends Comparable<? super T>> int binarySearch(T[] array, T target) {
        Objects.requireNonNull(array, "array must not be null");
        Objects.requireNonNull(target, "target must not be null");

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

    /**
     * Searches for {@code target} with optional sorted-order validation.
     *
     * <p>When {@code validateSorted} is {@code true}, this method first calls
     * {@link #isSorted(Comparable[])} and throws an {@link IllegalArgumentException}
     * if the array is not sorted in ascending natural order. When
     * {@code validateSorted} is {@code false}, this method behaves like
     * {@link #binarySearch(Comparable[], Comparable)}.</p>
     *
     * <p>The search phase runs in {@code O(log n)} time. Enabling validation adds
     * an {@code O(n)} sortedness check before the search.</p>
     *
     * @param array the array to search
     * @param target the value to find
     * @param validateSorted whether to verify sorted order before searching
     * @param <T> the element type, ordered by its natural {@link Comparable} implementation
     * @return the index of {@code target}, or {@code -1} when the target is absent
     * @throws NullPointerException if {@code array}, {@code target}, or any array element is {@code null}
     * @throws IllegalArgumentException if validation is enabled and {@code array} is not sorted
     */
    public static <T extends Comparable<? super T>> int binarySearch(
            T[] array,
            T target,
            boolean validateSorted) {
        Objects.requireNonNull(target, "target must not be null");

        if (validateSorted && !isSorted(array)) {
            throw new IllegalArgumentException("array must be sorted in ascending order");
        }

        return binarySearch(array, target);
    }

    /**
     * Checks whether {@code array} is sorted in ascending natural order.
     *
     * <p>This utility performs the validation that the fast
     * {@link #binarySearch(Comparable[], Comparable)} method intentionally skips.
     * Call this method when input may be unsorted and you want to validate before
     * searching.</p>
     *
     * <p>The generic type {@code T} must implement {@link Comparable}; ordering
     * is based on each element's {@link Comparable#compareTo(Object)} result.</p>
     *
     * <p>This method runs in {@code O(n)} time.</p>
     *
     * @param array the array to validate
     * @param <T> the element type, ordered by its natural {@link Comparable} implementation
     * @return {@code true} when {@code array} is sorted in ascending order; otherwise {@code false}
     * @throws NullPointerException if {@code array} or any array element is {@code null}
     */
    public static <T extends Comparable<? super T>> boolean isSorted(T[] array) {
        Objects.requireNonNull(array, "array must not be null");

        for (int index = 0; index < array.length; index++) {
            Objects.requireNonNull(array[index], "array elements must not be null");

            if (index > 0 && array[index - 1].compareTo(array[index]) > 0) {
                return false;
            }
        }

        return true;
    }
}
