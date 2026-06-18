import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class ServiceStatistics {
    private static final ServiceStatistics instance = new ServiceStatistics();
    private final AtomicLong totalRequests = new AtomicLong(0);
    private final AtomicLong successfulRequests = new AtomicLong(0);
    private final AtomicLong failedRequests = new AtomicLong(0);
    private final AtomicLong totalProcessingTimeMs = new AtomicLong(0);
    private final Map<String, AtomicLong> operationCounts = new java.util.concurrent.ConcurrentHashMap<>();
    private final Map<String, AtomicLong> operationTimes = new java.util.concurrent.ConcurrentHashMap<>();

    private ServiceStatistics() {}

    public static ServiceStatistics getInstance() {
        return instance;
    }

    public void recordRequest(String operationType, long processingTimeMs, boolean success) {
        totalRequests.incrementAndGet();
        totalProcessingTimeMs.addAndGet(processingTimeMs);

        if (success) {
            successfulRequests.incrementAndGet();
        } else {
            failedRequests.incrementAndGet();
        }

        operationCounts.computeIfAbsent(operationType, k -> new AtomicLong(0)).incrementAndGet();
        operationTimes.computeIfAbsent(operationType, k -> new AtomicLong(0)).addAndGet(processingTimeMs);
    }

    public long getTotalRequests() {
        return totalRequests.get();
    }

    public long getSuccessfulRequests() {
        return successfulRequests.get();
    }

    public long getFailedRequests() {
        return failedRequests.get();
    }

    public double getSuccessRate() {
        long total = totalRequests.get();
        return total > 0 ? (successfulRequests.get() * 100.0) / total : 0;
    }

    public double getAverageProcessingTimeMs() {
        long total = totalRequests.get();
        return total > 0 ? totalProcessingTimeMs.get() / (double) total : 0;
    }

    public Map<String, Object> getOperationStats(String operationType) {
        Map<String, Object> stats = new HashMap<>();
        long count = operationCounts.getOrDefault(operationType, new AtomicLong(0)).get();
        long totalTime = operationTimes.getOrDefault(operationType, new AtomicLong(0)).get();

        stats.put("count", count);
        stats.put("totalTimeMs", totalTime);
        stats.put("averageTimeMs", count > 0 ? totalTime / (double) count : 0);
        return stats;
    }

    public void printStatistics() {
        System.out.println("\n=== Service Statistics ===");
        System.out.println("Total Requests: " + totalRequests.get());
        System.out.println("Successful: " + successfulRequests.get());
        System.out.println("Failed: " + failedRequests.get());
        System.out.printf("Success Rate: %.2f%%%n", getSuccessRate());
        System.out.printf("Average Processing Time: %.2f ms%n", getAverageProcessingTimeMs());

        System.out.println("\nOperation Statistics:");
        for (Map.Entry<String, AtomicLong> entry : operationCounts.entrySet()) {
            Map<String, Object> stats = getOperationStats(entry.getKey());
            System.out.printf("  %s: %d calls, %.2f ms avg%n",
                entry.getKey(),
                stats.get("count"),
                stats.get("averageTimeMs"));
        }
    }

    public void reset() {
        totalRequests.set(0);
        successfulRequests.set(0);
        failedRequests.set(0);
        totalProcessingTimeMs.set(0);
        operationCounts.clear();
        operationTimes.clear();
        Logger.info("Service statistics reset");
    }
}
