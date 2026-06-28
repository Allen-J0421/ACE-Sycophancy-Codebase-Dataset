public final class SearchUtils {
    private SearchUtils() {
    }

    public static <T extends Comparable<? super T>> int binarySearch(T[] arr, T target) {
        return binarySearch(arr, target, 0, arr.length - 1);
    }

    public static <T extends Comparable<? super T>> int binarySearch(T[] arr, T target, int low, int high) {
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
}
