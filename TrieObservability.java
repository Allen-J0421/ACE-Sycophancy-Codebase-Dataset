import java.util.*;
import java.time.Instant;
import java.util.concurrent.CopyOnWriteArrayList;

// Advanced observability: tracing, logging, profiling

interface TrieSpan {
    void addTag(String key, String value);
    void addEvent(String eventName);
    void end();
}

class SimpleSpan implements TrieSpan {
    private final String operationName;
    private final Instant startTime;
    private final Map<String, String> tags;
    private final List<String> events;

    SimpleSpan(String operationName) {
        this.operationName = operationName;
        this.startTime = Instant.now();
        this.tags = new LinkedHashMap<>();
        this.events = new ArrayList<>();
    }

    @Override
    public void addTag(String key, String value) {
        tags.put(key, value);
    }

    @Override
    public void addEvent(String eventName) {
        events.add(eventName + " at " + System.currentTimeMillis());
    }

    @Override
    public void end() {
        long durationMs = System.currentTimeMillis() - startTime.toEpochMilli();
        System.out.println(String.format(
            "[TRACE] %s: %dms {tags=%s, events=%s}",
            operationName, durationMs, tags, events));
    }
}

interface TracingContext {
    TrieSpan startSpan(String operationName);
    TrieSpan getCurrentSpan();
    void endSpan();
}

class ThreadLocalTracingContext implements TracingContext {
    private final ThreadLocal<Stack<TrieSpan>> spanStack = ThreadLocal.withInitial(Stack::new);

    @Override
    public TrieSpan startSpan(String operationName) {
        TrieSpan span = new SimpleSpan(operationName);
        spanStack.get().push(span);
        return span;
    }

    @Override
    public TrieSpan getCurrentSpan() {
        Stack<TrieSpan> stack = spanStack.get();
        return stack.isEmpty() ? null : stack.peek();
    }

    @Override
    public void endSpan() {
        Stack<TrieSpan> stack = spanStack.get();
        if (!stack.isEmpty()) {
            TrieSpan span = stack.pop();
            span.end();
        }
    }
}

// Metrics system with aggregation

class MetricSnapshot {
    private final String name;
    private final double value;
    private final String unit;
    private final long timestamp;

    MetricSnapshot(String name, double value, String unit) {
        this.name = name;
        this.value = value;
        this.unit = unit;
        this.timestamp = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return String.format("%s=%%.2f%s", name, value, unit);
    }
}

class MetricsCollector {
    private final Map<String, List<Double>> metrics = new LinkedHashMap<>();
    private final Map<String, MetricSnapshot> latest = new LinkedHashMap<>();

    void record(String metricName, double value) {
        metrics.computeIfAbsent(metricName, k -> new ArrayList<>()).add(value);
        latest.put(metricName, new MetricSnapshot(metricName, value, ""));
    }

    double getAverage(String metricName) {
        List<Double> values = metrics.get(metricName);
        if (values == null || values.isEmpty()) return 0;
        return values.stream().mapToDouble(Double::doubleValue).average().orElse(0);
    }

    double getMax(String metricName) {
        List<Double> values = metrics.get(metricName);
        if (values == null || values.isEmpty()) return 0;
        return values.stream().mapToDouble(Double::doubleValue).max().orElse(0);
    }

    double getMin(String metricName) {
        List<Double> values = metrics.get(metricName);
        if (values == null || values.isEmpty()) return 0;
        return values.stream().mapToDouble(Double::doubleValue).min().orElse(0);
    }

    Map<String, Map<String, Double>> getSummary() {
        Map<String, Map<String, Double>> summary = new LinkedHashMap<>();
        for (String metric : metrics.keySet()) {
            Map<String, Double> stats = new LinkedHashMap<>();
            stats.put("average", getAverage(metric));
            stats.put("min", getMin(metric));
            stats.put("max", getMax(metric));
            stats.put("count", (double) metrics.get(metric).size());
            summary.put(metric, stats);
        }
        return summary;
    }

    void printSummary() {
        System.out.println("\n=== Metrics Summary ===");
        for (Map.Entry<String, Map<String, Double>> entry : getSummary().entrySet()) {
            System.out.println(entry.getKey() + ":");
            entry.getValue().forEach((k, v) -> System.out.println("  " + k + ": " + v));
        }
    }
}

// Profiling support

class ProfiledTrieDecorator extends TrieDecorator {
    private final MetricsCollector metricsCollector = new MetricsCollector();
    private final TracingContext tracingContext = new ThreadLocalTracingContext();

    ProfiledTrieDecorator(Trie delegate) {
        super(delegate);
    }

    @Override
    public OperationResult<Void> insert(String word) {
        TrieSpan span = tracingContext.startSpan("insert:" + word);
        try {
            long start = System.nanoTime();
            OperationResult<Void> result = delegate.insert(word);
            long durationMs = (System.nanoTime() - start) / 1_000_000;
            metricsCollector.record("insert_time_ms", durationMs);
            span.addTag("word", word);
            span.addTag("success", String.valueOf(result.isSuccess()));
            return result;
        } finally {
            tracingContext.endSpan();
        }
    }

    @Override
    public boolean search(String word) {
        TrieSpan span = tracingContext.startSpan("search:" + word);
        try {
            long start = System.nanoTime();
            boolean result = delegate.search(word);
            long durationMs = (System.nanoTime() - start) / 1_000_000;
            metricsCollector.record("search_time_ms", durationMs);
            span.addTag("word", word);
            span.addTag("found", String.valueOf(result));
            return result;
        } finally {
            tracingContext.endSpan();
        }
    }

    @Override
    public OperationResult<Boolean> delete(String word) {
        TrieSpan span = tracingContext.startSpan("delete:" + word);
        try {
            long start = System.nanoTime();
            OperationResult<Boolean> result = delegate.delete(word);
            long durationMs = (System.nanoTime() - start) / 1_000_000;
            metricsCollector.record("delete_time_ms", durationMs);
            span.addTag("word", word);
            span.addTag("success", String.valueOf(result.isSuccess()));
            return result;
        } finally {
            tracingContext.endSpan();
        }
    }

    public MetricsCollector getProfileMetrics() {
        return metricsCollector;
    }
}

// Structured logging

interface StructuredLogger {
    void log(LogLevel level, String message, Map<String, Object> context);
}

enum LogLevel {
    DEBUG, INFO, WARN, ERROR
}

class JSONStructuredLogger implements StructuredLogger {
    @Override
    public void log(LogLevel level, String message, Map<String, Object> context) {
        Map<String, Object> logEntry = new LinkedHashMap<>();
        logEntry.put("timestamp", Instant.now().toString());
        logEntry.put("level", level.toString());
        logEntry.put("message", message);
        logEntry.put("context", context);
        System.out.println(logEntry.toString());
    }
}

// Observability context

class ObservabilityContext {
    private final StructuredLogger logger;
    private final MetricsCollector metricsCollector;
    private final TracingContext tracingContext;

    ObservabilityContext() {
        this.logger = new JSONStructuredLogger();
        this.metricsCollector = new MetricsCollector();
        this.tracingContext = new ThreadLocalTracingContext();
    }

    StructuredLogger getLogger() {
        return logger;
    }

    MetricsCollector getMetricsCollector() {
        return metricsCollector;
    }

    TracingContext getTracingContext() {
        return tracingContext;
    }

    void logOperation(String operation, String result, Map<String, Object> details) {
        logger.log(LogLevel.INFO, "Operation: " + operation, details);
    }

    void recordMetric(String name, double value) {
        metricsCollector.record(name, value);
    }
}
