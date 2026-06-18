public class EnhancedSolveMetrics extends SolveMetrics {
  private final long startTime;
  private final long endTime;
  private final int cacheHits;
  private final int iterations;
  private final String solverVersion;

  public EnhancedSolveMetrics(long executionTimeNanos, String strategyName,
      int memoryUsageBytes, long startTime, long endTime,
      int cacheHits, int iterations, String solverVersion) {
    super(executionTimeNanos, strategyName, memoryUsageBytes);
    this.startTime = startTime;
    this.endTime = endTime;
    this.cacheHits = cacheHits;
    this.iterations = iterations;
    this.solverVersion = solverVersion;
  }

  public long getStartTime() {
    return startTime;
  }

  public long getEndTime() {
    return endTime;
  }

  public int getCacheHits() {
    return cacheHits;
  }

  public int getIterations() {
    return iterations;
  }

  public String getSolverVersion() {
    return solverVersion;
  }

  public double getTimePerIteration() {
    return iterations > 0 ? (double) getExecutionTimeNanos() / iterations : 0;
  }

  @Override
  public String toString() {
    return String.format(
        "EnhancedMetrics{strategy=%s, time=%dms, cacheHits=%d, " +
        "iterations=%d, timePerIter=%.2fμs, version=%s}",
        getStrategyName(), getExecutionTimeMillis(), cacheHits,
        iterations, getTimePerIteration() / 1000, solverVersion);
  }
}
