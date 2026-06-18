package sorting.analysis;

import java.util.Objects;

public record SortMetrics(
        long durationNanos,
        long comparisons,
        long swaps,
        int arraySize,
        String algorithmName) {

    public SortMetrics {
        Objects.requireNonNull(algorithmName);
        if (durationNanos < 0) {
            throw new IllegalArgumentException("Duration cannot be negative");
        }
        if (arraySize < 0) {
            throw new IllegalArgumentException("Array size cannot be negative");
        }
    }

    public double durationMs() {
        return durationNanos / 1_000_000.0;
    }

    public double durationSeconds() {
        return durationNanos / 1_000_000_000.0;
    }

    public double comparisonsPerElement() {
        return arraySize > 0 ? (double) comparisons / arraySize : 0;
    }

    public double swapsPerElement() {
        return arraySize > 0 ? (double) swaps / arraySize : 0;
    }

    public double operationsPerSecond() {
        if (durationNanos == 0) return 0;
        return (comparisons + swaps) / (durationNanos / 1_000_000_000.0);
    }

    public static class Builder {
        private long durationNanos;
        private long comparisons;
        private long swaps;
        private int arraySize;
        private String algorithmName;

        public Builder durationNanos(long nanos) {
            this.durationNanos = nanos;
            return this;
        }

        public Builder comparisons(long count) {
            this.comparisons = count;
            return this;
        }

        public Builder swaps(long count) {
            this.swaps = count;
            return this;
        }

        public Builder arraySize(int size) {
            this.arraySize = size;
            return this;
        }

        public Builder algorithmName(String name) {
            this.algorithmName = name;
            return this;
        }

        public SortMetrics build() {
            return new SortMetrics(durationNanos, comparisons, swaps, arraySize, algorithmName);
        }
    }

    @Override
    public String toString() {
        return String.format(
                "%s: %.3f ms | Size: %d | Comparisons: %d | Swaps: %d | Ops/sec: %.0f",
                algorithmName, durationMs(), arraySize, comparisons, swaps, operationsPerSecond());
    }
}
