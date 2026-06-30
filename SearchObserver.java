interface SearchObserver {
    void onSearchStarted(String strategyName, int itemCount);

    void onSearchCompleted(SearchExecutionMetrics metrics);

    void onSearchFailed(
            String strategyName,
            int itemCount,
            long elapsedNanos,
            RuntimeException exception);
}
