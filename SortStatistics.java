public class SortStatistics {
    private final long startTime;
    private long endTime;
    private long comparisons;
    private long swaps;

    public SortStatistics() {
        this.startTime = System.nanoTime();
        this.comparisons = 0;
        this.swaps = 0;
    }

    public void recordComparison() {
        this.comparisons++;
    }

    public void recordSwap() {
        this.swaps++;
    }

    public void end() {
        this.endTime = System.nanoTime();
    }

    public long getDurationNanos() {
        return endTime - startTime;
    }

    public long getComparisons() {
        return comparisons;
    }

    public long getSwaps() {
        return swaps;
    }

    @Override
    public String toString() {
        return String.format("Duration: %.2f ms | Comparisons: %d | Swaps: %d",
                getDurationNanos() / 1_000_000.0, comparisons, swaps);
    }
}
