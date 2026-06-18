import java.util.Objects;

public record SortResult(
        long durationNanos,
        long comparisons,
        long swaps,
        boolean successful,
        String message) {

    public SortResult {
        Objects.requireNonNull(message, "message cannot be null");
        if (durationNanos < 0) {
            throw new IllegalArgumentException("durationNanos must be non-negative");
        }
        if (comparisons < 0) {
            throw new IllegalArgumentException("comparisons must be non-negative");
        }
        if (swaps < 0) {
            throw new IllegalArgumentException("swaps must be non-negative");
        }
    }

    public double durationMs() {
        return durationNanos / 1_000_000.0;
    }

    public double comparisonsPerElement(int arraySize) {
        return arraySize > 0 ? (double) comparisons / arraySize : 0;
    }

    @Override
    public String toString() {
        return String.format(
                "SortResult{duration=%.3f ms, comparisons=%d, swaps=%d, successful=%s, msg='%s'}",
                durationMs(), comparisons, swaps, successful, message);
    }
}
