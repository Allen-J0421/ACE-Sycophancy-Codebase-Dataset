import java.util.Comparator;

final class RecursiveBinarySearchStrategy<T> implements SearchStrategy<T> {
    @Override
    public SearchResult search(T[] sortedArray, T target, Comparator<? super T> comparator) {
        return search(sortedArray, target, comparator, 0, sortedArray.length - 1);
    }

    private SearchResult search(
            T[] sortedArray,
            T target,
            Comparator<? super T> comparator,
            int low,
            int high) {
        if (low > high) {
            return SearchResult.notFound();
        }

        int mid = low + (high - low) / 2;
        int comparison = comparator.compare(sortedArray[mid], target);

        if (comparison == 0) {
            return SearchResult.foundAt(mid);
        }

        if (comparison < 0) {
            return search(sortedArray, target, comparator, mid + 1, high);
        }

        return search(sortedArray, target, comparator, low, mid - 1);
    }
}
