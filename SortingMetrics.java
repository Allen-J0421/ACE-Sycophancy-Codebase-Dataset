class SortingMetrics {
    private long comparisons = 0;
    private long swaps = 0;
    private long arrayAccesses = 0;
    private long startTimeNanos = 0;
    private long endTimeNanos = 0;

    public void recordComparison() {
        comparisons++;
    }

    public void recordSwap() {
        swaps++;
    }

    public void recordArrayAccess() {
        arrayAccesses++;
    }

    public void startTiming() {
        startTimeNanos = System.nanoTime();
    }

    public void endTiming() {
        endTimeNanos = System.nanoTime();
    }

    public long getComparisons() {
        return comparisons;
    }

    public long getSwaps() {
        return swaps;
    }

    public long getArrayAccesses() {
        return arrayAccesses;
    }

    public double getElapsedTimeMillis() {
        return (endTimeNanos - startTimeNanos) / 1_000_000.0;
    }

    public long getElapsedTimeNanos() {
        return endTimeNanos - startTimeNanos;
    }

    public void reset() {
        comparisons = 0;
        swaps = 0;
        arrayAccesses = 0;
        startTimeNanos = 0;
        endTimeNanos = 0;
    }

    @Override
    public String toString() {
        return String.format(
            "Metrics{comparisons=%d, swaps=%d, accesses=%d, time=%.3f ms}",
            comparisons, swaps, arrayAccesses, getElapsedTimeMillis()
        );
    }
}
