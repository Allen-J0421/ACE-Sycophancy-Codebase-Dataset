import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class SearchUtils {
    private SearchUtils() {
    }

    /**
     * Searches the full array for the target value.
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
        return binarySearch(arr, target, Comparator.naturalOrder());
    }

    /**
     * Searches the full array for the target value using the supplied comparator.
     *
     * <p>Time complexity: O(log n).</p>
     *
     * @param arr sorted array to search
     * @param target value to find
     * @param comparator comparison logic matching the array's sort order
     * @param <T> element type
     * @return index of the target, or -1 if it is not present
     * @throws IllegalArgumentException if the array or comparator is null
     */
    public static <T> int binarySearch(T[] arr, T target, Comparator<? super T> comparator) {
        validateArrayNotNull(arr);
        return binarySearch(Arrays.asList(arr), target, comparator);
    }

    /**
     * Searches the array for the target value within the inclusive index range.
     *
     * <p>Time complexity: O(log n), where n is the size of the selected range.</p>
     *
     * @param arr sorted array to search
     * @param target value to find
     * @param low inclusive lower bound
     * @param high inclusive upper bound
     * @param <T> comparable element type
     * @return index of the target, or -1 if it is not present
     * @throws IllegalArgumentException if the array is null or low is greater than high
     * @throws ArrayIndexOutOfBoundsException if either bound is outside the array
     */
    public static <T extends Comparable<? super T>> int binarySearch(T[] arr, T target, int low, int high) {
        return binarySearch(arr, target, low, high, Comparator.naturalOrder());
    }

    /**
     * Searches the array for the target value within the inclusive index range
     * using the supplied comparator.
     *
     * <p>Time complexity: O(log n), where n is the size of the selected range.</p>
     *
     * @param arr sorted array to search
     * @param target value to find
     * @param low inclusive lower bound
     * @param high inclusive upper bound
     * @param comparator comparison logic matching the array's sort order
     * @param <T> element type
     * @return index of the target, or -1 if it is not present
     * @throws IllegalArgumentException if the array or comparator is null, or low is greater than high
     * @throws ArrayIndexOutOfBoundsException if either bound is outside the array
     */
    public static <T> int binarySearch(T[] arr, T target, int low, int high, Comparator<? super T> comparator) {
        validateArrayNotNull(arr);
        return binarySearch(Arrays.asList(arr), target, low, high, comparator);
    }

    /**
     * Searches the full list for the target value.
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
        return binarySearch(values, target, Comparator.naturalOrder());
    }

    /**
     * Searches the full list for the target value using the supplied comparator.
     *
     * <p>Time complexity: O(log n) comparisons. Total access time depends on the
     * list implementation's indexed lookup cost.</p>
     *
     * @param values sorted list to search
     * @param target value to find
     * @param comparator comparison logic matching the list's sort order
     * @param <T> element type
     * @return index of the target, or -1 if it is not present
     * @throws IllegalArgumentException if the list or comparator is null
     */
    public static <T> int binarySearch(List<T> values, T target, Comparator<? super T> comparator) {
        validateListNotNull(values);
        validateComparatorNotNull(comparator);
        if (values.isEmpty())
            return -1;

        return normalizeSearchResult(Collections.binarySearch(values, target, comparator), 0);
    }

    /**
     * Searches the list for the target value within the inclusive index range.
     *
     * <p>Time complexity: O(log n) comparisons, where n is the size of the
     * selected range. Total access time depends on the list implementation's
     * indexed lookup cost.</p>
     *
     * @param values sorted list to search
     * @param target value to find
     * @param low inclusive lower bound
     * @param high inclusive upper bound
     * @param <T> comparable element type
     * @return index of the target, or -1 if it is not present
     * @throws IllegalArgumentException if the list is null or low is greater than high
     * @throws ArrayIndexOutOfBoundsException if either bound is outside the list
     */
    public static <T extends Comparable<? super T>> int binarySearch(List<T> values, T target, int low, int high) {
        return binarySearch(values, target, low, high, Comparator.naturalOrder());
    }

    /**
     * Searches the list for the target value within the inclusive index range
     * using the supplied comparator.
     *
     * <p>Time complexity: O(log n) comparisons, where n is the size of the
     * selected range. Total access time depends on the list implementation's
     * indexed lookup cost.</p>
     *
     * @param values sorted list to search
     * @param target value to find
     * @param low inclusive lower bound
     * @param high inclusive upper bound
     * @param comparator comparison logic matching the list's sort order
     * @param <T> element type
     * @return index of the target, or -1 if it is not present
     * @throws IllegalArgumentException if the list or comparator is null, or low is greater than high
     * @throws ArrayIndexOutOfBoundsException if either bound is outside the list
     */
    public static <T> int binarySearch(List<T> values, T target, int low, int high, Comparator<? super T> comparator) {
        validateListNotNull(values);
        validateComparatorNotNull(comparator);
        validateBounds(values.size(), low, high);

        List<T> range = values.subList(low, high + 1);
        return normalizeSearchResult(Collections.binarySearch(range, target, comparator), low);
    }

    /**
     * Returns whether the full array contains the target value.
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
     * Returns whether the full array contains the target value using the supplied comparator.
     *
     * <p>Time complexity: O(log n).</p>
     *
     * @param arr sorted array to search
     * @param target value to find
     * @param comparator comparison logic matching the array's sort order
     * @param <T> element type
     * @return true if the target is present, otherwise false
     * @throws IllegalArgumentException if the array or comparator is null
     */
    public static <T> boolean contains(T[] arr, T target, Comparator<? super T> comparator) {
        return binarySearch(arr, target, comparator) >= 0;
    }

    /**
     * Returns whether the array contains the target value within the inclusive index range.
     *
     * <p>Time complexity: O(log n), where n is the size of the selected range.</p>
     *
     * @param arr sorted array to search
     * @param target value to find
     * @param low inclusive lower bound
     * @param high inclusive upper bound
     * @param <T> comparable element type
     * @return true if the target is present, otherwise false
     * @throws IllegalArgumentException if the array is null or low is greater than high
     * @throws ArrayIndexOutOfBoundsException if either bound is outside the array
     */
    public static <T extends Comparable<? super T>> boolean contains(T[] arr, T target, int low, int high) {
        return binarySearch(arr, target, low, high) >= 0;
    }

    /**
     * Returns whether the array contains the target value within the inclusive index range
     * using the supplied comparator.
     *
     * <p>Time complexity: O(log n), where n is the size of the selected range.</p>
     *
     * @param arr sorted array to search
     * @param target value to find
     * @param low inclusive lower bound
     * @param high inclusive upper bound
     * @param comparator comparison logic matching the array's sort order
     * @param <T> element type
     * @return true if the target is present, otherwise false
     * @throws IllegalArgumentException if the array or comparator is null, or low is greater than high
     * @throws ArrayIndexOutOfBoundsException if either bound is outside the array
     */
    public static <T> boolean contains(T[] arr, T target, int low, int high, Comparator<? super T> comparator) {
        return binarySearch(arr, target, low, high, comparator) >= 0;
    }

    /**
     * Returns whether the full list contains the target value.
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
     * Returns whether the full list contains the target value using the supplied comparator.
     *
     * <p>Time complexity: O(log n) comparisons. Total access time depends on the
     * list implementation's indexed lookup cost.</p>
     *
     * @param values sorted list to search
     * @param target value to find
     * @param comparator comparison logic matching the list's sort order
     * @param <T> element type
     * @return true if the target is present, otherwise false
     * @throws IllegalArgumentException if the list or comparator is null
     */
    public static <T> boolean contains(List<T> values, T target, Comparator<? super T> comparator) {
        return binarySearch(values, target, comparator) >= 0;
    }

    /**
     * Returns whether the list contains the target value within the inclusive index range.
     *
     * <p>Time complexity: O(log n) comparisons, where n is the size of the
     * selected range. Total access time depends on the list implementation's
     * indexed lookup cost.</p>
     *
     * @param values sorted list to search
     * @param target value to find
     * @param low inclusive lower bound
     * @param high inclusive upper bound
     * @param <T> comparable element type
     * @return true if the target is present, otherwise false
     * @throws IllegalArgumentException if the list is null or low is greater than high
     * @throws ArrayIndexOutOfBoundsException if either bound is outside the list
     */
    public static <T extends Comparable<? super T>> boolean contains(List<T> values, T target, int low, int high) {
        return binarySearch(values, target, low, high) >= 0;
    }

    /**
     * Returns whether the list contains the target value within the inclusive index range
     * using the supplied comparator.
     *
     * <p>Time complexity: O(log n) comparisons, where n is the size of the
     * selected range. Total access time depends on the list implementation's
     * indexed lookup cost.</p>
     *
     * @param values sorted list to search
     * @param target value to find
     * @param low inclusive lower bound
     * @param high inclusive upper bound
     * @param comparator comparison logic matching the list's sort order
     * @param <T> element type
     * @return true if the target is present, otherwise false
     * @throws IllegalArgumentException if the list or comparator is null, or low is greater than high
     * @throws ArrayIndexOutOfBoundsException if either bound is outside the list
     */
    public static <T> boolean contains(List<T> values, T target, int low, int high, Comparator<? super T> comparator) {
        return binarySearch(values, target, low, high, comparator) >= 0;
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

    private static <T> void validateListNotNull(List<T> values) {
        if (values == null)
            throw new IllegalArgumentException("List must not be null");
    }

    private static <T> void validateComparatorNotNull(Comparator<? super T> comparator) {
        if (comparator == null)
            throw new IllegalArgumentException("Comparator must not be null");
    }

    private static void validateBounds(int size, int low, int high) {
        if (size == 0)
            throw new ArrayIndexOutOfBoundsException("Cannot search explicit bounds in an empty input");

        if (low < 0 || low >= size)
            throw new ArrayIndexOutOfBoundsException("Low index out of bounds: " + low);

        if (high < 0 || high >= size)
            throw new ArrayIndexOutOfBoundsException("High index out of bounds: " + high);

        if (low > high)
            throw new IllegalArgumentException("Low index must be less than or equal to high index");
    }
}
