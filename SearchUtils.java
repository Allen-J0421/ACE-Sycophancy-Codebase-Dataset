import java.util.Arrays;
import java.util.List;

public final class SearchUtils {
    private SearchUtils() {
    }

    public static <T extends Comparable<? super T>> int binarySearch(T[] arr, T target) {
        validateArrayNotNull(arr);
        return binarySearch(Arrays.asList(arr), target);
    }

    public static <T extends Comparable<? super T>> int binarySearch(T[] arr, T target, int low, int high) {
        validateArrayNotNull(arr);
        return binarySearch(Arrays.asList(arr), target, low, high);
    }

    public static <T extends Comparable<? super T>> int binarySearch(List<T> values, T target) {
        validateListNotNull(values);
        if (values.isEmpty())
            return -1;

        return binarySearch(values, target, 0, values.size() - 1);
    }

    public static <T extends Comparable<? super T>> int binarySearch(List<T> values, T target, int low, int high) {
        validateListNotNull(values);
        validateBounds(values.size(), low, high);

        return binarySearchInRange(values, target, low, high);
    }

    private static <T extends Comparable<? super T>> int binarySearchInRange(List<T> values, T target, int low, int high) {
        while (low <= high) {
            int mid = low + (high - low) / 2;
            int comparison = values.get(mid).compareTo(target);

            if (comparison == 0)
                return mid;

            if (comparison < 0)
                low = mid + 1;
            else
                high = mid - 1;
        }

        return -1;
    }

    private static <T> void validateArrayNotNull(T[] arr) {
        if (arr == null)
            throw new IllegalArgumentException("Array must not be null");
    }

    private static <T> void validateListNotNull(List<T> values) {
        if (values == null)
            throw new IllegalArgumentException("List must not be null");
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
