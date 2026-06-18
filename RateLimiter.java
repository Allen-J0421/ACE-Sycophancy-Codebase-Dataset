import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RateLimiter {
    private final int maxRequests;
    private final long timeWindowMs;
    private final Map<String, RequestBucket> buckets = new ConcurrentHashMap<>();

    public RateLimiter(int maxRequests, long timeWindowMs) {
        this.maxRequests = maxRequests;
        this.timeWindowMs = timeWindowMs;
    }

    public boolean allowRequest(String clientId) {
        RequestBucket bucket = buckets.computeIfAbsent(clientId, k -> new RequestBucket());
        return bucket.allowRequest();
    }

    public int getRemainingRequests(String clientId) {
        RequestBucket bucket = buckets.get(clientId);
        return bucket != null ? bucket.getRemainingRequests() : maxRequests;
    }

    public void reset(String clientId) {
        buckets.remove(clientId);
    }

    public void resetAll() {
        buckets.clear();
    }

    private class RequestBucket {
        private int requestCount = 0;
        private long windowStart = System.currentTimeMillis();

        synchronized boolean allowRequest() {
            long now = System.currentTimeMillis();
            if (now - windowStart > timeWindowMs) {
                requestCount = 0;
                windowStart = now;
            }

            if (requestCount < maxRequests) {
                requestCount++;
                return true;
            }
            return false;
        }

        synchronized int getRemainingRequests() {
            long now = System.currentTimeMillis();
            if (now - windowStart > timeWindowMs) {
                requestCount = 0;
                windowStart = now;
            }
            return Math.max(0, maxRequests - requestCount);
        }
    }
}
