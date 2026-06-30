final class NoOpSearchObserver implements SearchObserver {
    @Override
    public void onSearchStarted(String strategyName, int itemCount) {
    }

    @Override
    public void onSearchCompleted(SearchExecutionMetrics metrics) {
    }

    @Override
    public void onSearchFailed(
            String strategyName,
            int itemCount,
            long elapsedNanos,
            RuntimeException exception) {
    }
}
