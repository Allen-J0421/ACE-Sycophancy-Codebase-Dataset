public class RetryPolicy {
  private final int maxRetries;
  private final long delayMs;
  private final double backoffMultiplier;

  public RetryPolicy(int maxRetries, long delayMs) {
    this(maxRetries, delayMs, 1.0);
  }

  public RetryPolicy(int maxRetries, long delayMs, double backoffMultiplier) {
    this.maxRetries = maxRetries;
    this.delayMs = delayMs;
    this.backoffMultiplier = backoffMultiplier;
  }

  public <T> Result<T> execute(java.util.function.Supplier<T> operation) {
    int attempt = 0;
    long currentDelay = delayMs;

    while (attempt < maxRetries) {
      try {
        T result = operation.get();
        return new Result.Success<>(result);
      } catch (Exception e) {
        attempt++;
        if (attempt >= maxRetries) {
          return new Result.Failure<>(e);
        }

        try {
          Thread.sleep(currentDelay);
          currentDelay = (long) (currentDelay * backoffMultiplier);
        } catch (InterruptedException ie) {
          Thread.currentThread().interrupt();
          return new Result.Failure<>(ie);
        }
      }
    }

    return new Result.Failure<>("Max retries exceeded");
  }

  @Override
  public String toString() {
    return String.format("RetryPolicy{maxRetries=%d, delayMs=%d, backoff=%.2f}",
        maxRetries, delayMs, backoffMultiplier);
  }
}
