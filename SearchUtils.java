public final class SearchUtils {
    private SearchUtils() {
    }

    public static <T extends Comparable<? super T>> SearchResult binarySearch(T[] arr, T target) {
        return binarySearch(arr, target, 0, arr.length);
    }

    public static <T extends Comparable<? super T>> SearchResult binarySearch(
            T[] arr, T target, int fromIndex, int toIndex) {
        RangeValidator.validate(arr.length, fromIndex, toIndex);

        int low = fromIndex;
        int high = toIndex - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            int comparison = arr[mid].compareTo(target);

            if (comparison == 0) {
                return SearchResult.found(mid);
            }

            if (comparison < 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return SearchResult.notFound(low);
    }

    public static <T extends Comparable<? super T>> int binarySearchIndex(T[] arr, T target) {
        return binarySearch(arr, target).index();
    }

    public static <T extends Comparable<? super T>> int binarySearchIndex(
            T[] arr, T target, int fromIndex, int toIndex) {
        return binarySearch(arr, target, fromIndex, toIndex).index();
    }
}
