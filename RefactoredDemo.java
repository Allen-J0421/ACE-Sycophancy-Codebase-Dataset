import java.util.*;

public class RefactoredDemo {
    public static void main(String[] args) {
        demonstrateMiddleware();
        demonstrateDecorators();
        demonstrateHealthChecks();
        demonstrateModules();
        demonstrateCompleteSystem();
    }

    private static void demonstrateMiddleware() {
        System.out.println("=== Middleware Pipeline Demo ===\n");
        AdvancedTrie trie = new AdvancedTrie();

        MiddlewarePipeline pipeline = new MiddlewarePipeline()
            .addMiddleware(new LoggingMiddleware("System"))
            .addMiddleware(new ValidationMiddleware())
            .addMiddleware(new CachingMiddleware())
            .addMiddleware(new MetricsMiddleware());

        // Execute operations through middleware
        System.out.println("1. Insert operations:");
        pipeline.execute(new TrieRequest("insert", "hello"), trie);
        pipeline.execute(new TrieRequest("insert", "world"), trie);

        System.out.println("\n2. Search operations:");
        pipeline.execute(new TrieRequest("search", "hello"), trie);
        pipeline.execute(new TrieRequest("search", "hello"), trie);  // Cached
        pipeline.execute(new TrieRequest("search", "missing"), trie);

        System.out.println("\n3. Invalid operation:");
        pipeline.execute(new TrieRequest("search", ""), trie);
    }

    private static void demonstrateDecorators() {
        System.out.println("\n=== Decorator Pattern Demo ===\n");
        AdvancedTrie baseTrie = new AdvancedTrie();

        // Stack decorators
        System.out.println("Building decorated Trie...");
        Trie logged = TrieDecoratorFactory.withLogging(baseTrie, "Logger");
        Trie cached = TrieDecoratorFactory.withCaching(logged, 256);
        Trie tracked = TrieDecoratorFactory.withStatistics(cached);

        System.out.println("\nOperating on decorated Trie:");
        tracked.insert("apple");
        tracked.insert("application");
        tracked.insert("apply");
        tracked.search("apple");
        tracked.search("apple");  // Cached
        tracked.search("missing");
        tracked.findWordsWithPrefix("app");

        if (tracked instanceof StatisticsTrieDecorator) {
            StatisticsTrieDecorator stats = (StatisticsTrieDecorator) tracked;
            System.out.println("\nDecorator Statistics: " + stats.getStats());
        }
    }

    private static void demonstrateHealthChecks() {
        System.out.println("\n=== Health Check System Demo ===\n");
        AdvancedTrie trie = new AdvancedTrie();

        // Add some data
        for (int i = 0; i < 100; i++) {
            trie.insert("word" + i);
        }

        HealthMonitor monitor = new HealthMonitor(trie);

        System.out.println("Running health checks...");
        Map<String, HealthStatus> results = monitor.checkHealth();

        System.out.println("\nHealth Check Results:");
        for (Map.Entry<String, HealthStatus> entry : results.entrySet()) {
            System.out.println("  " + entry.getKey() + ": " + entry.getValue());
        }

        System.out.println("\nOverall System Status: " + monitor.getSystemStatus().getDescription());
    }

    private static void demonstrateModules() {
        System.out.println("\n=== Module System Demo ===\n");
        AdvancedTrie trie = new AdvancedTrie();
        ModuleManager manager = new ModuleManager(trie);

        // Register modules
        manager.registerModule(new CachingModule());
        manager.registerModule(new LoggingModule());
        manager.registerModule(new HealthCheckModule());
        manager.registerModule(new MetricsModule());

        // Initialize all modules
        manager.initializeAll();

        // Print module status
        manager.printModuleStatus();

        // Access module functionality
        System.out.println("\nModule count: " + manager.getModuleCount());

        // Shutdown all modules
        manager.shutdownAll();
    }

    private static void demonstrateCompleteSystem() {
        System.out.println("\n=== Complete System Integration ===\n");

        // Create base trie
        AdvancedTrie baseTrie = new AdvancedTrie();

        // Apply full decoration
        Trie trie = TrieDecoratorFactory.withAll(baseTrie, "CompleteSystem");

        // Set up middleware pipeline
        MiddlewarePipeline pipeline = new MiddlewarePipeline()
            .addMiddleware(new LoggingMiddleware("Pipeline"))
            .addMiddleware(new ValidationMiddleware())
            .addMiddleware(new MetricsMiddleware());

        // Set up health monitoring
        HealthMonitor healthMonitor = new HealthMonitor(baseTrie);

        System.out.println("1. Building vocabulary through middleware...");
        String[] words = {"reactive", "middleware", "decorator", "pipeline", "health"};
        for (String word : words) {
            pipeline.execute(new TrieRequest("insert", word), baseTrie);
        }

        System.out.println("\n2. Searching through decorated Trie...");
        trie.search("reactive");
        trie.search("reactive");  // Cached
        trie.findWordsWithPrefix("mid");

        System.out.println("\n3. Health check results:");
        for (Map.Entry<String, HealthStatus> entry : healthMonitor.checkHealth().entrySet()) {
            System.out.println("  " + entry.getKey() + ": " + entry.getValue().getStatus());
        }

        System.out.println("\n4. Final statistics:");
        TrieStatistics stats = baseTrie.getStatistics();
        System.out.println("  Total words: " + stats.getTotalWords());
        System.out.println("  Total nodes: " + stats.getTotalNodes());
        System.out.println("  Max depth: " + stats.getMaxDepth());

        if (trie instanceof StatisticsTrieDecorator) {
            StatisticsTrieDecorator decorator = (StatisticsTrieDecorator) trie;
            System.out.println("  Decorator stats: " + decorator.getStats());
        }

        System.out.println("\n=== System Demonstration Complete ===");
    }
}
