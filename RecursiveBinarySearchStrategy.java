import java.util.Comparator;

final class RecursiveBinarySearchStrategy<T> implements SearchStrategy<T> {
    private final SearchConfiguration configuration;

    RecursiveBinarySearchStrategy() {
        this(SearchConfiguration.fullArray());
    }

    RecursiveBinarySearchStrategy(SearchConfiguration configuration) {
        if (configuration == null) {
            throw new IllegalArgumentException("configuration must not be null");
        }

        this.configuration = configuration;
    }

    @Override
    public SearchResult search(T[] sortedArray, T target, Comparator<? super T> comparator) {
        return search(
                sortedArray,
                target,
                comparator,
                configuration.low(),
                configuration.highForArrayLength(sortedArray.length));
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
