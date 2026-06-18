import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class MetricsCollector {
    private static final MetricsCollector instance = new MetricsCollector();
    private final Map<String, AtomicLong> counters = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> timings = new ConcurrentHashMap<>();
    private final Map<String, Long> startTimes = new ConcurrentHashMap<>();

    private MetricsCollector() {}

    public static MetricsCollector getInstance() {
        return instance;
    }

    public void incrementCounter(String name) {
        counters.computeIfAbsent(name, k -> new AtomicLong(0)).incrementAndGet();
    }

    public void addToCounter(String name, long value) {
        counters.computeIfAbsent(name, k -> new AtomicLong(0)).addAndGet(value);
    }

    public void startTiming(String operation) {
        startTimes.put(operation, System.nanoTime());
    }

    public void recordTiming(String operation) {
        Long startTime = startTimes.remove(operation);
        if (startTime != null) {
            long duration = System.nanoTime() - startTime;
            timings.computeIfAbsent(operation, k -> new AtomicLong(0)).addAndGet(duration);
            incrementCounter(operation + ".count");
        }
    }

    public long getCounter(String name) {
        AtomicLong counter = counters.get(name);
        return counter != null ? counter.get() : 0;
    }

    public long getTimingNanos(String operation) {
        AtomicLong timing = timings.get(operation);
        return timing != null ? timing.get() : 0;
    }

    public double getAverageTimingMillis(String operation) {
        long totalNanos = getTimingNanos(operation);
        long count = getCounter(operation + ".count");
        return count > 0 ? (totalNanos / 1_000_000.0) / count : 0;
    }

    public void printMetrics() {
        System.out.println("\n=== Metrics Report ===");
        System.out.println("Counters:");
        for (Map.Entry<String, AtomicLong> entry : counters.entrySet()) {
            System.out.println("  " + entry.getKey() + ": " + entry.getValue().get());
        }
        System.out.println("\nTimings (average ms):");
        for (Map.Entry<String, AtomicLong> entry : timings.entrySet()) {
            String op = entry.getKey();
            double avgMs = getAverageTimingMillis(op);
            System.out.printf("  %s: %.3f ms%n", op, avgMs);
        }
    }

    public void reset() {
        counters.clear();
        timings.clear();
        startTimes.clear();
        Logger.info("Metrics reset");
    }

    public Map<String, Object> getMetricsSnapshot() {
        Map<String, Object> snapshot = new HashMap<>();
        snapshot.put("counters", new HashMap<>(counters));
        snapshot.put("timings", new HashMap<>(timings));
        return snapshot;
    }
}
