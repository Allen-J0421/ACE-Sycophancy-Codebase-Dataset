import java.util.Comparator;
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
     * @throws NullPointerException if {@code array}, {@code target}, or a compared array element is {@code null}
     */
    public static <T extends Comparable<? super T>> int binarySearch(T[] array, T target) {
        return binarySearch(array, target, Comparator.naturalOrder());
    }

    /**
     * Searches for {@code target} in the range {@code [fromIndex, toIndex)} using natural order.
     *
     * <p>The range follows the standard Java convention: {@code fromIndex} is
     * inclusive and {@code toIndex} is exclusive. The selected range must already
     * be sorted in ascending natural order. This fast-path method does not
     * validate sorted order.</p>
     *
     * <p>This method runs in {@code O(log n)} time over the selected range.</p>
     *
     * @param array the sorted array to search
     * @param target the value to find
     * @param fromIndex the first index to search, inclusive
     * @param toIndex the last index to search, exclusive
     * @param <T> the element type, ordered by its natural {@link Comparable} implementation
     * @return the index of {@code target}, or {@code -1} when the target is absent from the selected range
     * @throws NullPointerException if {@code array}, {@code target}, or a compared array element is {@code null}
     * @throws IllegalArgumentException if {@code fromIndex > toIndex}
     * @throws ArrayIndexOutOfBoundsException if {@code fromIndex < 0} or {@code toIndex > array.length}
     */
    public static <T extends Comparable<? super T>> int binarySearch(
            T[] array,
            T target,
            int fromIndex,
            int toIndex) {
        return binarySearch(array, target, Comparator.naturalOrder(), fromIndex, toIndex);
    }

    /**
     * Searches for {@code target} in {@code array} using binary search and a custom comparator.
     *
     * <p>The array must already be sorted in ascending order according to
     * {@code comparator}. This fast-path method does not validate sorted order.
     * The caller is responsible for ensuring the array is sorted before calling
     * this method. Use {@link #isSorted(Object[], Comparator)} before calling this
     * method, or use {@link #binarySearch(Object[], Object, Comparator, boolean)}
     * with validation enabled, when input safety is more important than avoiding
     * the validation scan.</p>
     *
     * <p>This overload supports values that do not implement {@link Comparable}
     * and cases where callers need an ordering different from natural order.</p>
     *
     * <p>This method runs in {@code O(log n)} time when the input array is
     * sorted according to {@code comparator}.</p>
     *
     * @param array the sorted array to search
     * @param target the value to find
     * @param comparator the comparator that defines array ordering and target matching
     * @param <T> the element type
     * @return the index of {@code target}, or {@code -1} when the target is absent
     * @throws NullPointerException if {@code array}, {@code target}, {@code comparator},
     *         or a compared array element is {@code null}
     */
    public static <T> int binarySearch(T[] array, T target, Comparator<? super T> comparator) {
        Objects.requireNonNull(array, "array must not be null");
        return binarySearch(array, target, comparator, 0, array.length);
    }

    /**
     * Searches for {@code target} in the range {@code [fromIndex, toIndex)} using a custom comparator.
     *
     * <p>The range follows the standard Java convention: {@code fromIndex} is
     * inclusive and {@code toIndex} is exclusive. The selected range must already
     * be sorted in ascending order according to {@code comparator}. This
     * fast-path method does not validate sorted order.</p>
     *
     * <p>This overload supports values that do not implement {@link Comparable}
     * and cases where callers need an ordering different from natural order.</p>
     *
     * <p>This method runs in {@code O(log n)} time over the selected range.</p>
     *
     * @param array the sorted array to search
     * @param target the value to find
     * @param comparator the comparator that defines array ordering and target matching
     * @param fromIndex the first index to search, inclusive
     * @param toIndex the last index to search, exclusive
     * @param <T> the element type
     * @return the index of {@code target}, or {@code -1} when the target is absent from the selected range
     * @throws NullPointerException if {@code array}, {@code target}, {@code comparator},
     *         or a compared array element is {@code null}
     * @throws IllegalArgumentException if {@code fromIndex > toIndex}
     * @throws ArrayIndexOutOfBoundsException if {@code fromIndex < 0} or {@code toIndex > array.length}
     */
    public static <T> int binarySearch(
            T[] array,
            T target,
            Comparator<? super T> comparator,
            int fromIndex,
            int toIndex) {
        Objects.requireNonNull(array, "array must not be null");
        Objects.requireNonNull(target, "target must not be null");
        Objects.requireNonNull(comparator, "comparator must not be null");
        checkRange(array.length, fromIndex, toIndex);

        int low = fromIndex;
        int high = toIndex - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            T current = Objects.requireNonNull(array[mid], "array elements must not be null");
            int comparison = comparator.compare(current, target);

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
        Objects.requireNonNull(array, "array must not be null");
        return binarySearch(array, target, Comparator.naturalOrder(), 0, array.length, validateSorted);
    }

    /**
     * Searches for {@code target} in the range {@code [fromIndex, toIndex)} with optional validation.
     *
     * <p>The range follows the standard Java convention: {@code fromIndex} is
     * inclusive and {@code toIndex} is exclusive. When {@code validateSorted} is
     * {@code true}, only the selected range is validated before searching.</p>
     *
     * <p>The search phase runs in {@code O(log n)} time over the selected range.
     * Enabling validation adds an {@code O(n)} sortedness check over that range.</p>
     *
     * @param array the array to search
     * @param target the value to find
     * @param fromIndex the first index to search, inclusive
     * @param toIndex the last index to search, exclusive
     * @param validateSorted whether to verify sorted order before searching
     * @param <T> the element type, ordered by its natural {@link Comparable} implementation
     * @return the index of {@code target}, or {@code -1} when the target is absent from the selected range
     * @throws NullPointerException if {@code array}, {@code target}, or any checked array element is {@code null}
     * @throws IllegalArgumentException if {@code fromIndex > toIndex}, or if validation is enabled and the range is not sorted
     * @throws ArrayIndexOutOfBoundsException if {@code fromIndex < 0} or {@code toIndex > array.length}
     */
    public static <T extends Comparable<? super T>> int binarySearch(
            T[] array,
            T target,
            int fromIndex,
            int toIndex,
            boolean validateSorted) {
        return binarySearch(array, target, Comparator.naturalOrder(), fromIndex, toIndex, validateSorted);
    }

    /**
     * Searches for {@code target} with optional sorted-order validation using a custom comparator.
     *
     * <p>When {@code validateSorted} is {@code true}, this method first calls
     * {@link #isSorted(Object[], Comparator)} and throws an {@link IllegalArgumentException}
     * if the array is not sorted according to {@code comparator}. When
     * {@code validateSorted} is {@code false}, this method behaves like
     * {@link #binarySearch(Object[], Object, Comparator)}.</p>
     *
     * <p>The search phase runs in {@code O(log n)} time. Enabling validation adds
     * an {@code O(n)} sortedness check before the search.</p>
     *
     * @param array the array to search
     * @param target the value to find
     * @param comparator the comparator that defines array ordering and target matching
     * @param validateSorted whether to verify sorted order before searching
     * @param <T> the element type
     * @return the index of {@code target}, or {@code -1} when the target is absent
     * @throws NullPointerException if {@code array}, {@code target}, {@code comparator},
     *         or a compared array element is {@code null}
     * @throws IllegalArgumentException if validation is enabled and {@code array} is not sorted
     */
    public static <T> int binarySearch(
            T[] array,
            T target,
            Comparator<? super T> comparator,
            boolean validateSorted) {
        Objects.requireNonNull(array, "array must not be null");
        return binarySearch(array, target, comparator, 0, array.length, validateSorted);
    }

    /**
     * Searches for {@code target} in the range {@code [fromIndex, toIndex)} with optional validation
     * using a custom comparator.
     *
     * <p>The range follows the standard Java convention: {@code fromIndex} is
     * inclusive and {@code toIndex} is exclusive. When {@code validateSorted} is
     * {@code true}, only the selected range is validated according to
     * {@code comparator} before searching.</p>
     *
     * <p>The search phase runs in {@code O(log n)} time over the selected range.
     * Enabling validation adds an {@code O(n)} sortedness check over that range.</p>
     *
     * @param array the array to search
     * @param target the value to find
     * @param comparator the comparator that defines array ordering and target matching
     * @param fromIndex the first index to search, inclusive
     * @param toIndex the last index to search, exclusive
     * @param validateSorted whether to verify sorted order before searching
     * @param <T> the element type
     * @return the index of {@code target}, or {@code -1} when the target is absent from the selected range
     * @throws NullPointerException if {@code array}, {@code target}, {@code comparator},
     *         or any checked array element is {@code null}
     * @throws IllegalArgumentException if {@code fromIndex > toIndex}, or if validation is enabled and the range is not sorted
     * @throws ArrayIndexOutOfBoundsException if {@code fromIndex < 0} or {@code toIndex > array.length}
     */
    public static <T> int binarySearch(
            T[] array,
            T target,
            Comparator<? super T> comparator,
            int fromIndex,
            int toIndex,
            boolean validateSorted) {
        Objects.requireNonNull(array, "array must not be null");
        Objects.requireNonNull(target, "target must not be null");
        Objects.requireNonNull(comparator, "comparator must not be null");
        checkRange(array.length, fromIndex, toIndex);

        if (validateSorted && !isSorted(array, comparator, fromIndex, toIndex)) {
            throw new IllegalArgumentException("array must be sorted in ascending order");
        }

        return binarySearch(array, target, comparator, fromIndex, toIndex);
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
        return isSorted(array, Comparator.naturalOrder());
    }

    /**
     * Checks whether {@code array} is sorted in ascending order according to a custom comparator.
     *
     * <p>This utility performs the validation that the fast comparator-based
     * binary search method intentionally skips. Call this method when input may
     * be unsorted and you want to validate before searching.</p>
     *
     * <p>This overload supports values that do not implement {@link Comparable}
     * and cases where callers need an ordering different from natural order.</p>
     *
     * <p>This method runs in {@code O(n)} time.</p>
     *
     * @param array the array to validate
     * @param comparator the comparator that defines sorted order
     * @param <T> the element type
     * @return {@code true} when {@code array} is sorted according to {@code comparator}; otherwise {@code false}
     * @throws NullPointerException if {@code array}, {@code comparator}, or any array element is {@code null}
     */
    public static <T> boolean isSorted(T[] array, Comparator<? super T> comparator) {
        Objects.requireNonNull(array, "array must not be null");
        Objects.requireNonNull(comparator, "comparator must not be null");
        return isSorted(array, comparator, 0, array.length);
    }

    private static <T> boolean isSorted(
            T[] array,
            Comparator<? super T> comparator,
            int fromIndex,
            int toIndex) {
        for (int index = fromIndex; index < toIndex; index++) {
            Objects.requireNonNull(array[index], "array elements must not be null");

            if (index > fromIndex && comparator.compare(array[index - 1], array[index]) > 0) {
                return false;
            }
        }

        return true;
    }

    private static void checkRange(int arrayLength, int fromIndex, int toIndex) {
        if (fromIndex > toIndex) {
            throw new IllegalArgumentException(
                    "fromIndex (" + fromIndex + ") must not be greater than toIndex (" + toIndex + ")");
        }

        if (fromIndex < 0) {
            throw new ArrayIndexOutOfBoundsException("fromIndex (" + fromIndex + ") must not be negative");
        }

        if (toIndex > arrayLength) {
            throw new ArrayIndexOutOfBoundsException(
                    "toIndex (" + toIndex + ") must not be greater than array length (" + arrayLength + ")");
        }
    }
}
