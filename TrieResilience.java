import java.util.*;
import java.util.concurrent.*;

// Resilience patterns: retry, circuit breaker, timeout, fallback

@FunctionalInterface
interface ResilienceOperation {
    Object execute() throws Exception;
}

interface ResiliencePolicy {
    Object execute(ResilienceOperation operation) throws Exception;
}

class RetryPolicy implements ResiliencePolicy {
    private final int maxRetries;
    private final long delayMs;
    private final double backoffMultiplier;

    RetryPolicy(int maxRetries, long delayMs, double backoffMultiplier) {
        this.maxRetries = maxRetries;
        this.delayMs = delayMs;
        this.backoffMultiplier = backoffMultiplier;
    }

    @Override
    public Object execute(ResilienceOperation operation) throws Exception {
        int attempt = 0;
        long currentDelay = delayMs;

        while (true) {
            try {
                return operation.execute();
            } catch (Exception e) {
                attempt++;
                if (attempt > maxRetries) {
                    throw e;
                }
                System.out.println("[RetryPolicy] Retry " + attempt + " after " + currentDelay + "ms");
                Thread.sleep(currentDelay);
                currentDelay = (long) (currentDelay * backoffMultiplier);
            }
        }
    }
}

enum CircuitState {
    CLOSED("Operating normally"),
    OPEN("Circuit open, requests rejected"),
    HALF_OPEN("Testing recovery");

    private final String description;

    CircuitState(String description) {
        this.description = description;
    }

    String getDescription() {
        return description;
    }
}

class CircuitBreakerPolicy implements ResiliencePolicy {
    private CircuitState state;
    private int failureCount;
    private final int failureThreshold;
    private final long openDurationMs;
    private long lastFailureTime;
    private final String name;

    CircuitBreakerPolicy(String name, int failureThreshold, long openDurationMs) {
        this.name = name;
        this.failureThreshold = failureThreshold;
        this.openDurationMs = openDurationMs;
        this.state = CircuitState.CLOSED;
        this.failureCount = 0;
        this.lastFailureTime = 0;
    }

    @Override
    public Object execute(ResilienceOperation operation) throws Exception {
        if (state == CircuitState.OPEN) {
            if (System.currentTimeMillis() - lastFailureTime > openDurationMs) {
                state = CircuitState.HALF_OPEN;
                System.out.println("[CircuitBreaker] " + name + " -> HALF_OPEN");
            } else {
                throw new TrieException("Circuit breaker OPEN for " + name);
            }
        }

        try {
            Object result = operation.execute();
            if (state == CircuitState.HALF_OPEN) {
                state = CircuitState.CLOSED;
                failureCount = 0;
                System.out.println("[CircuitBreaker] " + name + " -> CLOSED");
            }
            return result;
        } catch (Exception e) {
            failureCount++;
            lastFailureTime = System.currentTimeMillis();

            if (failureCount >= failureThreshold) {
                state = CircuitState.OPEN;
                System.out.println("[CircuitBreaker] " + name + " -> OPEN");
            }
            throw e;
        }
    }

    CircuitState getState() {
        return state;
    }

    int getFailureCount() {
        return failureCount;
    }
}

class TimeoutPolicy implements ResiliencePolicy {
    private final long timeoutMs;

    TimeoutPolicy(long timeoutMs) {
        this.timeoutMs = timeoutMs;
    }

    @Override
    public Object execute(ResilienceOperation operation) throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            Future<?> future = executor.submit(operation::execute);
            return future.get(timeoutMs, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            throw new TrieException("Operation timed out after " + timeoutMs + "ms");
        } finally {
            executor.shutdown();
        }
    }
}

class FallbackPolicy {
    private final Object fallbackValue;

    FallbackPolicy(Object fallbackValue) {
        this.fallbackValue = fallbackValue;
    }

    <T> T executeWithFallback(Callable<T> operation) {
        try {
            return operation.call();
        } catch (Exception e) {
            System.out.println("[Fallback] Using fallback value due to: " + e.getMessage());
            return (T) fallbackValue;
        }
    }
}

class RateLimiter {
    private final int maxOperations;
    private final long windowMs;
    private final Queue<Long> operationTimestamps;

    RateLimiter(int maxOperations, long windowMs) {
        this.maxOperations = maxOperations;
        this.windowMs = windowMs;
        this.operationTimestamps = new ConcurrentLinkedQueue<>();
    }

    boolean allowOperation() {
        long now = System.currentTimeMillis();
        long windowStart = now - windowMs;

        // Remove old timestamps outside the window
        operationTimestamps.removeIf(timestamp -> timestamp < windowStart);

        if (operationTimestamps.size() < maxOperations) {
            operationTimestamps.offer(now);
            return true;
        }

        return false;
    }

    int getOperationsInWindow() {
        long now = System.currentTimeMillis();
        long windowStart = now - windowMs;
        operationTimestamps.removeIf(timestamp -> timestamp < windowStart);
        return operationTimestamps.size();
    }

    double getUtilization() {
        return (double) getOperationsInWindow() / maxOperations;
    }
}

// Bulkhead pattern - resource isolation
class Bulkhead {
    private final int maxConcurrent;
    private final Semaphore semaphore;
    private final String name;

    Bulkhead(String name, int maxConcurrent) {
        this.name = name;
        this.maxConcurrent = maxConcurrent;
        this.semaphore = new Semaphore(maxConcurrent);
    }

    <T> T execute(Callable<T> operation) throws Exception {
        if (!semaphore.tryAcquire()) {
            throw new TrieException("Bulkhead " + name + " at capacity");
        }

        try {
            return operation.call();
        } finally {
            semaphore.release();
        }
    }

    int getAvailableSlots() {
        return semaphore.availablePermits();
    }

    double getUtilization() {
        return (double) (maxConcurrent - getAvailableSlots()) / maxConcurrent;
    }
}

// Resilience policy chain
class ResiliencePolicyChain {
    private final List<ResiliencePolicy> policies = new ArrayList<>();
    private CircuitBreakerPolicy circuitBreaker;
    private RateLimiter rateLimiter;

    ResiliencePolicyChain addPolicy(ResiliencePolicy policy) {
        policies.add(policy);
        if (policy instanceof CircuitBreakerPolicy) {
            this.circuitBreaker = (CircuitBreakerPolicy) policy;
        }
        return this;
    }

    ResiliencePolicyChain withCircuitBreaker(String name, int threshold, long durationMs) {
        this.circuitBreaker = new CircuitBreakerPolicy(name, threshold, durationMs);
        policies.add(circuitBreaker);
        return this;
    }

    ResiliencePolicyChain withRateLimiter(int maxOps, long windowMs) {
        this.rateLimiter = new RateLimiter(maxOps, windowMs);
        return this;
    }

    Object execute(ResilienceOperation operation) throws Exception {
        if (rateLimiter != null && !rateLimiter.allowOperation()) {
            throw new TrieException("Rate limit exceeded");
        }

        Object result = operation.execute();
        for (ResiliencePolicy policy : policies) {
            // Apply policies in order
        }
        return result;
    }

    CircuitBreakerPolicy getCircuitBreaker() {
        return circuitBreaker;
    }

    RateLimiter getRateLimiter() {
        return rateLimiter;
    }
}
