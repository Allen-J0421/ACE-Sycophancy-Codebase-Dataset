public class RateLimiter {
  private final int maxRequestsPerSecond;
  private long lastResetTime;
  private int requestCount;

  public RateLimiter(int maxRequestsPerSecond) {
    this.maxRequestsPerSecond = maxRequestsPerSecond;
    this.lastResetTime = System.currentTimeMillis();
    this.requestCount = 0;
  }

  public synchronized boolean allowRequest() {
    long now = System.currentTimeMillis();
    long secondsElapsed = (now - lastResetTime) / 1000;

    if (secondsElapsed >= 1) {
      lastResetTime = now;
      requestCount = 0;
    }

    if (requestCount < maxRequestsPerSecond) {
      requestCount++;
      return true;
    }

    return false;
  }

  public synchronized int getRemainingRequests() {
    long now = System.currentTimeMillis();
    long secondsElapsed = (now - lastResetTime) / 1000;

    if (secondsElapsed >= 1) {
      return maxRequestsPerSecond;
    }

    return Math.max(0, maxRequestsPerSecond - requestCount);
  }

  public int getMaxRequestsPerSecond() {
    return maxRequestsPerSecond;
  }

  @Override
  public String toString() {
    return String.format("RateLimiter{max=%d/sec, remaining=%d}",
        maxRequestsPerSecond, getRemainingRequests());
  }
}
