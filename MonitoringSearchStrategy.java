import java.util.Comparator;

final class MonitoringSearchStrategy<T> implements SearchStrategy<T> {
    private static final int UNKNOWN_ITEM_COUNT = -1;

    private final SearchStrategy<T> delegate;
    private final SearchObserver observer;
    private final String strategyName;

    MonitoringSearchStrategy(SearchStrategy<T> delegate, SearchObserver observer) {
        this(delegate, observer, delegateName(delegate));
    }

    MonitoringSearchStrategy(
            SearchStrategy<T> delegate,
            SearchObserver observer,
            String strategyName) {
        if (delegate == null) {
            throw new IllegalArgumentException("delegate must not be null");
        }

        if (observer == null) {
            throw new IllegalArgumentException("observer must not be null");
        }

        if (strategyName == null) {
            throw new IllegalArgumentException("strategyName must not be null");
        }

        this.delegate = delegate;
        this.observer = observer;
        this.strategyName = strategyName;
    }

    @Override
    public SearchResult search(T[] sortedArray, T target, Comparator<? super T> comparator) {
        int itemCount = itemCount(sortedArray);
        observer.onSearchStarted(strategyName, itemCount);

        long startedAt = System.nanoTime();
        try {
            SearchResult result = delegate.search(sortedArray, target, comparator);
            long elapsedNanos = System.nanoTime() - startedAt;
            observer.onSearchCompleted(new SearchExecutionMetrics(
                    strategyName,
                    itemCount,
                    result,
                    elapsedNanos));
            return result;
        } catch (RuntimeException exception) {
            long elapsedNanos = System.nanoTime() - startedAt;
            observer.onSearchFailed(strategyName, itemCount, elapsedNanos, exception);
            throw exception;
        }
    }

    private static int itemCount(Object[] sortedArray) {
        if (sortedArray == null) {
            return UNKNOWN_ITEM_COUNT;
        }

        return sortedArray.length;
    }

    private static String delegateName(SearchStrategy<?> delegate) {
        if (delegate == null) {
            return "UnknownSearchStrategy";
        }

        return delegate.getClass().getSimpleName();
    }
}
