public class PerformanceMetrics {
    private long findCount;
    private long unionCount;
    private long startTime;

    public PerformanceMetrics() {
        this.findCount = 0;
        this.unionCount = 0;
        this.startTime = System.currentTimeMillis();
    }

    public void recordFind() {
        findCount++;
    }

    public void recordUnion() {
        unionCount++;
    }

    public long getElapsedTime() {
        return System.currentTimeMillis() - startTime;
    }

    public long getFindCount() {
        return findCount;
    }

    public long getUnionCount() {
        return unionCount;
    }

    public long getTotalOperations() {
        return findCount + unionCount;
    }

    public double getAverageOperationTime() {
        long total = getTotalOperations();
        if (total == 0) return 0;
        return (double) getElapsedTime() / total;
    }

    public void reset() {
        findCount = 0;
        unionCount = 0;
        startTime = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return String.format(
            "Metrics{finds=%d, unions=%d, total=%d, elapsed=%dms, avgTime=%.2fms}",
            findCount, unionCount, getTotalOperations(), getElapsedTime(), getAverageOperationTime()
        );
    }
}
