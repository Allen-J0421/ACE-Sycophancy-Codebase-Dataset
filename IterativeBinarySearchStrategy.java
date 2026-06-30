import java.util.Comparator;

final class IterativeBinarySearchStrategy<T> implements SearchStrategy<T> {
    private final SearchConfiguration configuration;

    IterativeBinarySearchStrategy() {
        this(SearchConfiguration.fullArray());
    }

    IterativeBinarySearchStrategy(SearchConfiguration configuration) {
        if (configuration == null) {
            throw new IllegalArgumentException("configuration must not be null");
        }

        this.configuration = configuration;
    }

    @Override
    public SearchResult search(T[] sortedArray, T target, Comparator<? super T> comparator) {
        int low = configuration.low();
        int high = configuration.highForArrayLength(sortedArray.length);

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
