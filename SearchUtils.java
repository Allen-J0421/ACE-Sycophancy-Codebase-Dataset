import java.util.Comparator;

public final class SearchUtils {
    private SearchUtils() {
    }

    public static <T extends Comparable<? super T>> SearchResult binarySearch(T[] arr, T target) {
        return binarySearch(arr, target, 0, arr.length, Comparator.naturalOrder());
    }

    public static <T> SearchResult binarySearch(
            T[] arr, T target, Comparator<? super T> comparator) {
        return binarySearch(arr, target, 0, arr.length, comparator);
    }

    public static <T extends Comparable<? super T>> SearchResult binarySearch(
            T[] arr, T target, int fromIndex, int toIndex) {
        return binarySearch(arr, target, fromIndex, toIndex, Comparator.naturalOrder());
    }

    public static <T> SearchResult binarySearch(
            T[] arr, T target, int fromIndex, int toIndex, Comparator<? super T> comparator) {
        RangeValidator.validate(arr.length, fromIndex, toIndex);

        Comparator<? super T> resolvedComparator = resolveComparator(comparator);
        int low = fromIndex;
        int high = toIndex - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            int comparison = resolvedComparator.compare(arr[mid], target);

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

    public static <T> int binarySearchIndex(
            T[] arr, T target, Comparator<? super T> comparator) {
        return binarySearch(arr, target, comparator).index();
    }

    public static <T extends Comparable<? super T>> int binarySearchIndex(
            T[] arr, T target, int fromIndex, int toIndex) {
        return binarySearch(arr, target, fromIndex, toIndex).index();
    }

    public static <T> int binarySearchIndex(
            T[] arr, T target, int fromIndex, int toIndex, Comparator<? super T> comparator) {
        return binarySearch(arr, target, fromIndex, toIndex, comparator).index();
    }

    private static <T> Comparator<? super T> resolveComparator(Comparator<? super T> comparator) {
        if (comparator != null) {
            return comparator;
        }

        return SearchUtils::compareNaturally;
    }

    @SuppressWarnings("unchecked")
    private static <T> int compareNaturally(T left, T right) {
        if (!(left instanceof Comparable<?> comparable)) {
            throw new IllegalArgumentException("a comparator is required for non-comparable elements");
        }

        return ((Comparable<? super T>) comparable).compareTo(right);
    }

    private static final class RangeValidator {
        private RangeValidator() {
        }

        static void validate(int length, int fromIndex, int toIndex) {
            if (fromIndex > toIndex) {
                throw new IllegalArgumentException("fromIndex must be less than or equal to toIndex");
            }

            if (fromIndex < 0 || toIndex > length) {
                throw new IndexOutOfBoundsException("range must be within array bounds");
            }
        }
    }
}
