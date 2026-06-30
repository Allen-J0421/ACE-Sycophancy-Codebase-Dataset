final class SearchStrategies {
    private SearchStrategies() {
    }

    static <T> SearchStrategy<T> iterativeBinarySearch() {
        return new IterativeBinarySearchStrategy<T>();
    }

    static <T> SearchStrategy<T> recursiveBinarySearch() {
        return new RecursiveBinarySearchStrategy<T>();
    }

    static <T> SearchStrategy<T> monitored(
            SearchStrategy<T> strategy,
            SearchObserver observer) {
        return new MonitoringSearchStrategy<T>(strategy, observer);
    }

    static <T> SearchStrategy<T> monitored(
            SearchStrategy<T> strategy,
            SearchObserver observer,
            String strategyName) {
        return new MonitoringSearchStrategy<T>(strategy, observer, strategyName);
    }

    static <T> SearchStrategy<T> monitoredIterativeBinarySearch(SearchObserver observer) {
        return monitored(iterativeBinarySearch(), observer);
    }

    static <T> SearchStrategy<T> monitoredRecursiveBinarySearch(SearchObserver observer) {
        return monitored(recursiveBinarySearch(), observer);
    }
}
