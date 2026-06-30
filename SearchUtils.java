public final class SearchUtils {
    private SearchUtils() {
    }

    public static <T extends Comparable<? super T>> int binarySearch(T[] arr, T target) {
        return binarySearch(arr, target, 0, arr.length);
    }

    public static <T extends Comparable<? super T>> int binarySearch(
            T[] arr, T target, int fromIndex, int toIndex) {
        checkRange(arr.length, fromIndex, toIndex);

        int low = fromIndex;
        int high = toIndex - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            int comparison = arr[mid].compareTo(target);

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

    private static void checkRange(int length, int fromIndex, int toIndex) {
        if (fromIndex > toIndex) {
            throw new IllegalArgumentException("fromIndex must be less than or equal to toIndex");
        }

        if (fromIndex < 0 || toIndex > length) {
            throw new IndexOutOfBoundsException("range must be within array bounds");
        }
    }
}
