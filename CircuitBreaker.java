import java.util.concurrent.atomic.AtomicLong;

public class CircuitBreaker {
    public enum State {
        CLOSED, OPEN, HALF_OPEN
    }

    private volatile State state = State.CLOSED;
    private final int failureThreshold;
    private final long timeoutMillis;
    private final AtomicLong failureCount = new AtomicLong(0);
    private volatile long lastFailureTime = 0;
    private final String name;

    public CircuitBreaker(String name, int failureThreshold, long timeoutMillis) {
        if (failureThreshold <= 0 || timeoutMillis <= 0) {
            throw new IllegalArgumentException("Parameters must be positive");
        }
        this.name = name;
        this.failureThreshold = failureThreshold;
        this.timeoutMillis = timeoutMillis;
    }

    public <T> T execute(Operation<T> operation) throws Exception {
        if (state == State.OPEN) {
            if (System.currentTimeMillis() - lastFailureTime > timeoutMillis) {
                state = State.HALF_OPEN;
                Logger.info("CircuitBreaker " + name + " transitioning to HALF_OPEN");
            } else {
                throw new CircuitBreakerOpenException(name + " is OPEN");
            }
        }

        try {
            T result = operation.execute();
            onSuccess();
            return result;
        } catch (Exception e) {
            onFailure();
            throw e;
        }
    }

    private void onSuccess() {
        failureCount.set(0);
        if (state == State.HALF_OPEN) {
            state = State.CLOSED;
            Logger.info("CircuitBreaker " + name + " closed");
        }
    }

    private void onFailure() {
        lastFailureTime = System.currentTimeMillis();
        long failures = failureCount.incrementAndGet();

        if (failures >= failureThreshold && state != State.OPEN) {
            state = State.OPEN;
            Logger.warn("CircuitBreaker " + name + " opened after " + failures + " failures");
        }
    }

    public State getState() {
        return state;
    }

    public long getFailureCount() {
        return failureCount.get();
    }

    public void reset() {
        state = State.CLOSED;
        failureCount.set(0);
        lastFailureTime = 0;
        Logger.info("CircuitBreaker " + name + " reset");
    }

    public interface Operation<T> {
        T execute() throws Exception;
    }

    public static class CircuitBreakerOpenException extends Exception {
        public CircuitBreakerOpenException(String message) {
            super(message);
        }
    }
}
