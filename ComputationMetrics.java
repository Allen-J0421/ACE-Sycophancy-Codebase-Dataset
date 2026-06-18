/**
 * Performance metrics for a prefix sum computation.
 */
public class ComputationMetrics {
    private final long computationTimeMs;
    private final int computationCount;
    private final long memoryUsedBytes;

    public ComputationMetrics(long computationTimeMs, int computationCount, long memoryUsedBytes) {
        this.computationTimeMs = computationTimeMs;
        this.computationCount = computationCount;
        this.memoryUsedBytes = memoryUsedBytes;
    }

    public long getComputationTimeMs() {
        return computationTimeMs;
    }

    public int getComputationCount() {
        return computationCount;
    }

    public long getMemoryUsedBytes() {
        return memoryUsedBytes;
    }

    public double getAverageTimePerComputationMs() {
        return computationCount > 0 ? (double) computationTimeMs / computationCount : 0.0;
    }

    @Override
    public String toString() {
        return "ComputationMetrics{" +
                "computationTimeMs=" + computationTimeMs +
                ", computationCount=" + computationCount +
                ", memoryUsedBytes=" + memoryUsedBytes +
                ", averageTimeMs=" + String.format("%.3f", getAverageTimePerComputationMs()) +
                '}';
    }
}
