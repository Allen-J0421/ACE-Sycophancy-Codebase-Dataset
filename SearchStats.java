/**
 * Statistics about a binary search operation.
 * Tracks metadata about search performance and behavior.
 */
class SearchStats {
    private final int comparisons;
    private final long elapsedNanos;

    SearchStats(int comparisons, long elapsedNanos) {
        this.comparisons = comparisons;
        this.elapsedNanos = elapsedNanos;
    }

    int getComparisons() {
        return comparisons;
    }

    long getElapsedNanos() {
        return elapsedNanos;
    }

    long getElapsedMicros() {
        return elapsedNanos / 1000;
    }

    @Override
    public String toString() {
        return String.format("Comparisons: %d, Time: %d ns (%.2f μs)",
            comparisons, elapsedNanos, elapsedNanos / 1000.0);
    }
}
