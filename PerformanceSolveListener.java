public class PerformanceSolveListener implements SolveEventListener {
  private long beforeTime;
  private long totalTime = 0;
  private int callCount = 0;

  @Override
  public void onEvent(SolveEvent event) {
    switch (event.getType()) {
      case BEFORE_SOLVE:
        beforeTime = System.nanoTime();
        break;
      case AFTER_SOLVE:
        long elapsed = System.nanoTime() - beforeTime;
        totalTime += elapsed;
        callCount++;
        break;
      case ERROR:
        break;
    }
  }

  public long getTotalTimeNanos() {
    return totalTime;
  }

  public int getCallCount() {
    return callCount;
  }

  public double getAverageTimeMillis() {
    return callCount > 0 ? (totalTime / 1_000_000.0) / callCount : 0;
  }

  public void reset() {
    totalTime = 0;
    callCount = 0;
  }

  @Override
  public String toString() {
    return String.format("Performance: %d calls, %.2fms total, %.4fms avg",
        callCount, totalTime / 1_000_000.0, getAverageTimeMillis());
  }
}
