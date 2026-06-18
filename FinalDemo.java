import java.util.*;

public class FinalDemo {
    public static void main(String[] args) {
        demonstrateConfiguration();
        demonstrateResilience();
        demonstrateBenchmarking();
        demonstrateObservability();
        demonstrateIntegratedSystem();
    }

    private static void demonstrateConfiguration() {
        System.out.println("=== Configuration Management Demo ===\n");

        // Using different configuration profiles
        System.out.println("1. Development Profile:");
        TrieConfiguration devConfig = TrieConfigProfile.development();
        System.out.println("  Cache size: " + devConfig.get("cache.size", 0));
        System.out.println("  Max memory: " + devConfig.get("health.maxMemoryMB", 0) + "MB");

        System.out.println("\n2. Production Profile:");
        TrieConfiguration prodConfig = TrieConfigProfile.production();
        System.out.println("  Cache size: " + prodConfig.get("cache.size", 0));
        System.out.println("  Max memory: " + prodConfig.get("health.maxMemoryMB", 0) + "MB");
        System.out.println("  Max trie size: " + prodConfig.get("health.maxTrieSize", 0));

        System.out.println("\n3. Custom Configuration:");
        TrieConfiguration customConfig = new TrieConfigBuilder()
            .withCacheSize(2048)
            .withMaxWordLength(50)
            .withMaxMemoryMB(256)
            .build();
        System.out.println("  Cache size: " + customConfig.get("cache.size", 0));
        System.out.println("  Max word length: " + customConfig.get("word.maxLength", 0));
    }

    private static void demonstrateResilience() {
        System.out.println("\n\n=== Resilience Patterns Demo ===\n");

        // Rate Limiter
        System.out.println("1. Rate Limiter (5 ops per 1 second):");
        RateLimiter rateLimiter = new RateLimiter(5, 1000);
        for (int i = 0; i < 7; i++) {
            boolean allowed = rateLimiter.allowOperation();
            System.out.println("  Operation " + (i + 1) + ": " + (allowed ? "ALLOWED" : "DENIED"));
        }
        System.out.println("  Utilization: " + String.format("%.0f%%", rateLimiter.getUtilization() * 100));

        // Circuit Breaker
        System.out.println("\n2. Circuit Breaker (threshold: 3 failures):");
        CircuitBreakerPolicy breaker = new CircuitBreakerPolicy("TestService", 3, 5000);
        System.out.println("  Initial state: " + breaker.getState().getDescription());

        // Simulate failures
        for (int i = 0; i < 4; i++) {
            final int index = i;
            try {
                breaker.execute(() -> {
                    throw new Exception("Service error");
                });
            } catch (Exception e) {
                System.out.println("  Failure " + (index + 1) + ": " + breaker.getState().getDescription());
            }
        }

        // Bulkhead
        System.out.println("\n3. Bulkhead (max 2 concurrent operations):");
        Bulkhead bulkhead = new Bulkhead("TestBulkhead", 2);
        for (int i = 0; i < 3; i++) {
            final int opIndex = i;
            try {
                boolean success = bulkhead.execute(() -> {
                    System.out.println("  Operation " + (opIndex + 1) + " executing");
                    return true;
                });
            } catch (Exception e) {
                System.out.println("  Operation " + (opIndex + 1) + ": " + e.getMessage());
            }
        }
        System.out.println("  Available slots: " + bulkhead.getAvailableSlots());
    }

    private static void demonstrateBenchmarking() {
        System.out.println("\n\n=== Benchmarking Demo ===\n");

        String[] testWords = {"apple", "application", "apply", "appreciate", "approval"};
        String[] testPrefixes = {"app", "apr"};

        BenchmarkRunner runner = new BenchmarkRunner(100, 1000);

        System.out.println("Running benchmarks...");
        runner.run("Insert Benchmark", new TrieInsertBenchmark(testWords));
        runner.run("Search Benchmark", new TrieSearchBenchmark(testWords));
        runner.run("Prefix Search Benchmark", new TriePrefixSearchBenchmark(testPrefixes));
        runner.run("Cache Effectiveness", new CacheEffectivenessBenchmark(testWords));

        runner.printResults();
    }

    private static void demonstrateObservability() {
        System.out.println("\n\n=== Observability Demo ===\n");

        System.out.println("1. Tracing:");
        TracingContext tracingContext = new ThreadLocalTracingContext();
        TrieSpan span = tracingContext.startSpan("sample_operation");
        span.addTag("operation", "insert");
        span.addTag("word", "example");
        span.addEvent("operation_started");
        span.addEvent("processing");
        tracingContext.endSpan();

        System.out.println("\n2. Metrics Collection:");
        MetricsCollector collector = new MetricsCollector();
        double[] sampleTimes = {1.5, 2.3, 1.8, 2.5, 3.1};
        for (double time : sampleTimes) {
            collector.record("operation_time_ms", time);
        }
        System.out.println("  Average time: " + String.format("%.2f", collector.getAverage("operation_time_ms")) + "ms");
        System.out.println("  Min time: " + String.format("%.2f", collector.getMin("operation_time_ms")) + "ms");
        System.out.println("  Max time: " + String.format("%.2f", collector.getMax("operation_time_ms")) + "ms");

        System.out.println("\n3. Structured Logging:");
        StructuredLogger logger = new JSONStructuredLogger();
        Map<String, Object> context = new LinkedHashMap<>();
        context.put("user", "admin");
        context.put("operation", "insert");
        context.put("status", "success");
        logger.log(LogLevel.INFO, "Operation completed", context);

        System.out.println("\n4. Profiled Trie:");
        Trie baseTrie = new Trie();
        ProfiledTrieDecorator profiled = new ProfiledTrieDecorator(baseTrie);
        profiled.insert("profiled");
        profiled.search("profiled");
        profiled.search("missing");
        System.out.println("  Profiled operations completed");
    }

    private static void demonstrateIntegratedSystem() {
        System.out.println("\n\n=== Fully Integrated System Demo ===\n");

        // Set up configuration
        TrieConfiguration config = TrieConfigProfile.production();

        // Create Trie with all features
        Trie baseTrie = new AdvancedTrie();

        // Apply profiling
        ProfiledTrieDecorator profiled = new ProfiledTrieDecorator(baseTrie);

        // Set up observability
        ObservabilityContext observability = new ObservabilityContext();

        System.out.println("1. Building vocabulary with observability:");
        String[] vocabulary = {"reactive", "resilience", "resilient", "resource", "reserve"};
        for (String word : vocabulary) {
            profiled.insert(word);
            Map<String, Object> context = new LinkedHashMap<>();
            context.put("word", word);
            context.put("action", "insert");
            observability.logOperation("insert", "success", context);
        }

        System.out.println("\n2. Searching with metrics:");
        String[] searchTerms = {"reactive", "missing", "resilience"};
        for (String term : searchTerms) {
            boolean found = profiled.search(term);
            observability.recordMetric("search_result", found ? 1 : 0);
        }

        System.out.println("\n3. System Summary:");
        System.out.println("  Configuration Profile: " + (config.has("health.maxMemoryMB") ? "Production" : "Unknown"));
        System.out.println("  Trie Size: " + baseTrie.getSize());
        System.out.println("  Statistics: " + baseTrie.getStatistics());

        System.out.println("\n4. Performance Metrics:");

        System.out.println("\n5. Health Status:");
        HealthMonitor monitor = new HealthMonitor(baseTrie);
        for (Map.Entry<String, HealthStatus> entry : monitor.checkHealth().entrySet()) {
            System.out.println("  " + entry.getKey() + ": " + entry.getValue().getStatus());
        }

        System.out.println("\n=== System Demonstration Complete ===");
    }
}
