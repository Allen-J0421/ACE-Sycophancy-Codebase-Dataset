import java.util.Comparator;

final class IterativeBinarySearchStrategy<T> implements SearchStrategy<T> {
    @Override
    public SearchResult search(T[] sortedArray, T target, Comparator<? super T> comparator) {
        int low = 0;
        int high = sortedArray.length - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            int comparison = comparator.compare(sortedArray[mid], target);

            if (comparison == 0) {
                return SearchResult.foundAt(mid);
            }

            if (comparison < 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return SearchResult.notFound();
    }
}
