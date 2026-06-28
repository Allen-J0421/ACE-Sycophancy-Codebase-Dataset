public final class SearchUtils {
    private SearchUtils() {
    }

    public static <T extends Comparable<? super T>> int binarySearch(T[] arr, T target) {
        validateArray(arr);
        if (arr.length == 0)
            return -1;

        return binarySearch(arr, target, 0, arr.length - 1);
    }

    public static <T extends Comparable<? super T>> int binarySearch(T[] arr, T target, int low, int high) {
        validateArray(arr);
        validateRange(arr, low, high);

        while (low <= high) {
            int mid = low + (high - low) / 2;
            int comparison = arr[mid].compareTo(target);

            if (comparison == 0)
                return mid;

            if (comparison < 0)
                low = mid + 1;
            else
                high = mid - 1;
        }

        return -1;
    }

    private static <T> void validateArray(T[] arr) {
        if (arr == null)
            throw new IllegalArgumentException("Array must not be null");
    }

    private static <T> void validateRange(T[] arr, int low, int high) {
        if (arr.length == 0)
            throw new ArrayIndexOutOfBoundsException("Cannot search explicit bounds in an empty array");

        if (low < 0 || low >= arr.length)
            throw new ArrayIndexOutOfBoundsException("Low index out of bounds: " + low);

        if (high < 0 || high >= arr.length)
            throw new ArrayIndexOutOfBoundsException("High index out of bounds: " + high);

        if (low > high)
            throw new IllegalArgumentException("Low index must be less than or equal to high index");
    }
}
