final class SearchStrategies {
    private SearchStrategies() {
    }

    static <T> SearchStrategy<T> iterativeBinarySearch() {
        return iterativeBinarySearch(SearchConfiguration.fullArray());
    }

    static <T> SearchStrategy<T> iterativeBinarySearch(SearchConfiguration configuration) {
        return new IterativeBinarySearchStrategy<T>(configuration);
    }

    static <T> SearchStrategy<T> recursiveBinarySearch() {
        return recursiveBinarySearch(SearchConfiguration.fullArray());
    }

    static <T> SearchStrategy<T> recursiveBinarySearch(SearchConfiguration configuration) {
        return new RecursiveBinarySearchStrategy<T>(configuration);
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

    static <T> SearchStrategy<T> monitoredIterativeBinarySearch(
            SearchObserver observer,
            SearchConfiguration configuration) {
        return monitored(iterativeBinarySearch(configuration), observer);
    }

    static <T> SearchStrategy<T> monitoredRecursiveBinarySearch(SearchObserver observer) {
        return monitored(recursiveBinarySearch(), observer);
    }

    static <T> SearchStrategy<T> monitoredRecursiveBinarySearch(
            SearchObserver observer,
            SearchConfiguration configuration) {
        return monitored(recursiveBinarySearch(configuration), observer);
    }
}
