import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class SearchUtils {
    private SearchUtils() {
    }

    /**
     * Inclusive index range for bounded searches.
     *
     * <p>Pass null as the range to search the full input.</p>
     */
    public static final class Range {
        private final int low;
        private final int high;

        private Range(int low, int high) {
            this.low = low;
            this.high = high;
        }

        public static Range of(int low, int high) {
            return new Range(low, high);
        }

        public int low() {
            return low;
        }

        public int high() {
            return high;
        }
    }

    /**
     * Searches the full array for the target value using natural ordering.
     *
     * <p>Time complexity: O(log n).</p>
     *
     * @param arr sorted array to search
     * @param target value to find
     * @param <T> comparable element type
     * @return index of the target, or -1 if it is not present
     * @throws IllegalArgumentException if the array is null
     */
    public static <T extends Comparable<? super T>> int binarySearch(T[] arr, T target) {
        return binarySearch(arr, target, Comparator.naturalOrder(), null);
    }

    /**
     * Searches the array using the supplied comparator and optional range.
     *
     * <p>Time complexity: O(log n), where n is the searched range size. Pass null
     * for range to search the full array.</p>
     *
     * @param arr sorted array to search
     * @param target value to find
     * @param comparator comparison logic matching the array's sort order
     * @param range inclusive range to search, or null for the full array
     * @param <T> element type
     * @return index of the target, or -1 if it is not present
     * @throws IllegalArgumentException if the array or comparator is null, or the range is inverted
     * @throws ArrayIndexOutOfBoundsException if either range bound is outside the array
     */
    public static <T> int binarySearch(T[] arr, T target, Comparator<? super T> comparator, Range range) {
        validateArrayNotNull(arr);
        return binarySearch(Arrays.asList(arr), target, comparator, range);
    }

    /**
     * Searches the full list for the target value using natural ordering.
     *
     * <p>Time complexity: O(log n) comparisons. Total access time depends on the
     * list implementation's indexed lookup cost.</p>
     *
     * @param values sorted list to search
     * @param target value to find
     * @param <T> comparable element type
     * @return index of the target, or -1 if it is not present
     * @throws IllegalArgumentException if the list is null
     */
    public static <T extends Comparable<? super T>> int binarySearch(List<T> values, T target) {
        return binarySearch(values, target, Comparator.naturalOrder(), null);
    }

    /**
     * Searches the list using the supplied comparator and optional range.
     *
     * <p>Time complexity: O(log n) comparisons, where n is the searched range
     * size. Total access time depends on the list implementation's indexed lookup
     * cost. Pass null for range to search the full list.</p>
     *
     * @param values sorted list to search
     * @param target value to find
     * @param comparator comparison logic matching the list's sort order
     * @param range inclusive range to search, or null for the full list
     * @param <T> element type
     * @return index of the target, or -1 if it is not present
     * @throws IllegalArgumentException if the list or comparator is null, or the range is inverted
     * @throws ArrayIndexOutOfBoundsException if either range bound is outside the list
     */
    public static <T> int binarySearch(List<T> values, T target, Comparator<? super T> comparator, Range range) {
        validateSearchInputs(values, comparator);
        if (values.isEmpty()) {
            if (range == null)
                return -1;
            throw new ArrayIndexOutOfBoundsException("Cannot search explicit bounds in an empty input");
        }

        SearchWindow<T> window = resolveWindow(values, range);
        int result = Collections.binarySearch(window.values, target, comparator);
        return normalizeSearchResult(result, window.offset);
    }

    /**
     * Returns whether the full array contains the target value using natural ordering.
     *
     * <p>Time complexity: O(log n).</p>
     *
     * @param arr sorted array to search
     * @param target value to find
     * @param <T> comparable element type
     * @return true if the target is present, otherwise false
     * @throws IllegalArgumentException if the array is null
     */
    public static <T extends Comparable<? super T>> boolean contains(T[] arr, T target) {
        return binarySearch(arr, target) >= 0;
    }

    /**
     * Returns whether the array contains the target value using the supplied
     * comparator and optional range.
     *
     * <p>Time complexity: O(log n), where n is the searched range size. Pass null
     * for range to search the full array.</p>
     *
     * @param arr sorted array to search
     * @param target value to find
     * @param comparator comparison logic matching the array's sort order
     * @param range inclusive range to search, or null for the full array
     * @param <T> element type
     * @return true if the target is present, otherwise false
     * @throws IllegalArgumentException if the array or comparator is null, or the range is inverted
     * @throws ArrayIndexOutOfBoundsException if either range bound is outside the array
     */
    public static <T> boolean contains(T[] arr, T target, Comparator<? super T> comparator, Range range) {
        return binarySearch(arr, target, comparator, range) >= 0;
    }

    /**
     * Returns whether the full list contains the target value using natural ordering.
     *
     * <p>Time complexity: O(log n) comparisons. Total access time depends on the
     * list implementation's indexed lookup cost.</p>
     *
     * @param values sorted list to search
     * @param target value to find
     * @param <T> comparable element type
     * @return true if the target is present, otherwise false
     * @throws IllegalArgumentException if the list is null
     */
    public static <T extends Comparable<? super T>> boolean contains(List<T> values, T target) {
        return binarySearch(values, target) >= 0;
    }

    /**
     * Returns whether the list contains the target value using the supplied
     * comparator and optional range.
     *
     * <p>Time complexity: O(log n) comparisons, where n is the searched range
     * size. Total access time depends on the list implementation's indexed lookup
     * cost. Pass null for range to search the full list.</p>
     *
     * @param values sorted list to search
     * @param target value to find
     * @param comparator comparison logic matching the list's sort order
     * @param range inclusive range to search, or null for the full list
     * @param <T> element type
     * @return true if the target is present, otherwise false
     * @throws IllegalArgumentException if the list or comparator is null, or the range is inverted
     * @throws ArrayIndexOutOfBoundsException if either range bound is outside the list
     */
    public static <T> boolean contains(List<T> values, T target, Comparator<? super T> comparator, Range range) {
        return binarySearch(values, target, comparator, range) >= 0;
    }

    private static <T> SearchWindow<T> resolveWindow(List<T> values, Range range) {
        if (range == null)
            return new SearchWindow<>(values, 0);

        validateBounds(values.size(), range);
        return new SearchWindow<>(values.subList(range.low(), range.high() + 1), range.low());
    }

    private static int normalizeSearchResult(int result, int offset) {
        if (result < 0)
            return -1;

        return result + offset;
    }

    private static <T> void validateArrayNotNull(T[] arr) {
        if (arr == null)
            throw new IllegalArgumentException("Array must not be null");
    }

    private static <T> void validateSearchInputs(List<T> values, Comparator<? super T> comparator) {
        if (values == null)
            throw new IllegalArgumentException("List must not be null");

        if (comparator == null)
            throw new IllegalArgumentException("Comparator must not be null");
    }

    private static void validateBounds(int size, Range range) {
        if (range.low() < 0 || range.low() >= size)
            throw new ArrayIndexOutOfBoundsException("Low index out of bounds: " + range.low());

        if (range.high() < 0 || range.high() >= size)
            throw new ArrayIndexOutOfBoundsException("High index out of bounds: " + range.high());

        if (range.low() > range.high())
            throw new IllegalArgumentException("Low index must be less than or equal to high index");
    }

    private static final class SearchWindow<T> {
        private final List<T> values;
        private final int offset;

        private SearchWindow(List<T> values, int offset) {
            this.values = values;
            this.offset = offset;
        }
    }
}
