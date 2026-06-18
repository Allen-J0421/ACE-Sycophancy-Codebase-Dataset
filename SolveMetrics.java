public class SolveMetrics {
  private final long executionTimeNanos;
  private final String strategyName;
  private final int memoryUsageBytes;

  public SolveMetrics(long executionTimeNanos, String strategyName, int memoryUsageBytes) {
    this.executionTimeNanos = executionTimeNanos;
    this.strategyName = strategyName;
    this.memoryUsageBytes = memoryUsageBytes;
  }

  public long getExecutionTimeNanos() {
    return executionTimeNanos;
  }

  public long getExecutionTimeMillis() {
    return executionTimeNanos / 1_000_000;
  }

  public String getStrategyName() {
    return strategyName;
  }

  public int getMemoryUsageBytes() {
    return memoryUsageBytes;
  }

  public double getMemoryUsageKB() {
    return memoryUsageBytes / 1024.0;
  }

  @Override
  public String toString() {
    return String.format(
        "SolveMetrics{strategy=%s, time=%dms, memory=%.2fKB}",
        strategyName, getExecutionTimeMillis(), getMemoryUsageKB());
  }
}
