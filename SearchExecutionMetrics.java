final class SearchExecutionMetrics {
    private final String strategyName;
    private final int itemCount;
    private final SearchResult result;
    private final long elapsedNanos;

    SearchExecutionMetrics(
            String strategyName,
            int itemCount,
            SearchResult result,
            long elapsedNanos) {
        if (strategyName == null) {
            throw new IllegalArgumentException("strategyName must not be null");
        }

        if (result == null) {
            throw new IllegalArgumentException("result must not be null");
        }

        this.strategyName = strategyName;
        this.itemCount = itemCount;
        this.result = result;
        this.elapsedNanos = elapsedNanos;
    }

    String strategyName() {
        return strategyName;
    }

    int itemCount() {
        return itemCount;
    }

    SearchResult result() {
        return result;
    }

    long elapsedNanos() {
        return elapsedNanos;
    }

    boolean found() {
        return result.isFound();
    }
}
