public class RetryPolicy {
    private final int maxRetries;
    private final long retryDelayMillis;
    private final double backoffMultiplier;
    private final long maxBackoffMillis;

    public RetryPolicy(int maxRetries, long retryDelayMillis) {
        this(maxRetries, retryDelayMillis, 1.0, retryDelayMillis);
    }

    public RetryPolicy(int maxRetries, long retryDelayMillis, double backoffMultiplier, long maxBackoffMillis) {
        if (maxRetries < 0 || retryDelayMillis < 0) {
            throw new IllegalArgumentException("Retry parameters must be non-negative");
        }
        this.maxRetries = maxRetries;
        this.retryDelayMillis = retryDelayMillis;
        this.backoffMultiplier = backoffMultiplier;
        this.maxBackoffMillis = maxBackoffMillis;
    }

    public <T> T executeWithRetry(Operation<T> operation) throws Exception {
        int attempts = 0;
        long delay = retryDelayMillis;

        while (true) {
            try {
                Logger.debug("Executing operation (attempt " + (attempts + 1) + ")");
                return operation.execute();
            } catch (Exception e) {
                attempts++;
                if (attempts > maxRetries) {
                    Logger.error("Operation failed after " + attempts + " attempts", e);
                    throw e;
                }

                Logger.warn("Operation failed, retrying in " + delay + "ms");
                Thread.sleep(delay);
                delay = Math.min((long)(delay * backoffMultiplier), maxBackoffMillis);
            }
        }
    }

    public interface Operation<T> {
        T execute() throws Exception;
    }

    public static class Builder {
        private int maxRetries = 3;
        private long retryDelayMillis = 100;
        private double backoffMultiplier = 1.0;
        private long maxBackoffMillis = 10000;

        public Builder maxRetries(int retries) {
            this.maxRetries = retries;
            return this;
        }

        public Builder retryDelayMillis(long delay) {
            this.retryDelayMillis = delay;
            return this;
        }

        public Builder exponentialBackoff(double multiplier, long maxDelay) {
            this.backoffMultiplier = multiplier;
            this.maxBackoffMillis = maxDelay;
            return this;
        }

        public RetryPolicy build() {
            return new RetryPolicy(maxRetries, retryDelayMillis, backoffMultiplier, maxBackoffMillis);
        }
    }
}
