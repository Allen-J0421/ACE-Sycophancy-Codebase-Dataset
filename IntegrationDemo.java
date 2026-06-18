import java.util.*;

public class IntegrationDemo {
    public static void main(String[] args) {
        demonstrateFullIntegration();
        demonstrateAdvancedArchitecture();
        demonstrateComplexWorkflow();
    }

    private static void demonstrateFullIntegration() {
        System.out.println("=== Full Integration Demo ===");
        AdvancedTrie trie = new AdvancedTrie();

        // Set up event handlers
        EventBus eventBus = trie.getEventBus();
        LoggingEventHandler logger = new LoggingEventHandler("System");
        MetricsEventHandler metrics = new MetricsEventHandler();

        eventBus.subscribe(logger);
        eventBus.subscribe(metrics);

        // Execute commands through service
        System.out.println("\n1. Executing insert commands...");
        trie.executeCommand(new InsertWordCommand("reactive"));
        trie.executeCommand(new InsertWordCommand("event"));
        trie.executeCommand(new InsertWordCommand("driven"));

        System.out.println("\n2. Executing search commands...");
        trie.executeCommand(new SearchWordCommand("reactive"));
        trie.executeCommand(new SearchWordCommand("missing"));
        trie.executeCommand(new SearchWordCommand("event"));

        System.out.println("\n3. Executing delete command...");
        trie.executeCommand(new DeleteWordCommand("reactive"));

        // Display results
        System.out.println("\n4. Event-based metrics:");
        System.out.println("   " + metrics);

        System.out.println("\n5. Undo/Redo capability:");
        CommandExecutor executor = trie.getCommandExecutor();
        System.out.println("   Undo stack size: " + executor.getHistory().getUndoStackSize());

        System.out.println("\n6. Service layer analytics:");
        Map<String, Object> analytics = trie.getAnalyticsViaService();
        analytics.forEach((k, v) -> System.out.println("   " + k + ": " + v));
    }

    private static void demonstrateAdvancedArchitecture() {
        System.out.println("\n=== Advanced Architecture Demo ===");
        AdvancedTrie trie = new AdvancedTrie();

        // Register custom service
        ServiceRegistry registry = trie.getServiceContainer().getRegistry();
        System.out.println("Registered services: " + registry.getServiceCount());

        // Use command pattern with interceptors
        CommandExecutor executor = trie.getCommandExecutor();
        TimingInterceptor timing = new TimingInterceptor();
        executor.addInterceptor(timing);

        System.out.println("\nExecuting commands with timing:");
        long start = System.nanoTime();
        trie.executeCommand(new InsertWordCommand("architecture"));
        trie.executeCommand(new InsertWordCommand("design"));
        trie.executeCommand(new InsertWordCommand("pattern"));
        long duration = System.nanoTime() - start;

        System.out.println("Total duration: " + (duration / 1_000_000.0) + " ms");
        System.out.println("Command timings:");
        timing.getExecutionTimes().forEach((cmd, time) ->
            System.out.println("  " + cmd + ": " + time + "ms")
        );

        // Demonstrate undo/redo
        System.out.println("\nUndo/Redo demonstration:");
        System.out.println("Words before undo: " + trie.getAllWords());
        executor.undo(trie);
        System.out.println("Words after undo: " + trie.getAllWords());
        executor.redo(trie);
        System.out.println("Words after redo: " + trie.getAllWords());
    }

    private static void demonstrateComplexWorkflow() {
        System.out.println("\n=== Complex Workflow Demo ===");
        AdvancedTrie trie = new AdvancedTrie();

        // Set up comprehensive event handling
        EventBus eventBus = trie.getEventBus();
        MetricsEventHandler metricsHandler = new MetricsEventHandler();
        eventBus.subscribe(metricsHandler);

        // Simulate complex workflow
        System.out.println("Step 1: Building initial vocabulary...");
        String[] words = {"apple", "app", "application", "apply", "approve"};
        for (String word : words) {
            trie.executeCommand(new InsertWordCommand(word));
        }

        System.out.println("Step 2: Searching for words...");
        String[] searchTerms = {"app", "apple", "apricot", "approval", "apply"};
        for (String term : searchTerms) {
            trie.executeCommand(new SearchWordCommand(term));
        }

        System.out.println("Step 3: Using search service...");
        SearchService searchService = trie.getServiceContainer()
            .getRegistry()
            .getService(SearchService.class);
        List<String> prefixResults = searchService.findByPrefix("app", trie);
        System.out.println("  Words with 'app' prefix: " + prefixResults);

        System.out.println("Step 4: Analytics...");
        AnalyticsService analyticsService = trie.getServiceContainer()
            .getRegistry()
            .getService(AnalyticsService.class);
        TrieStatistics stats = analyticsService.getStatistics(trie);
        System.out.println("  " + stats);

        System.out.println("Step 5: Event metrics summary...");
        System.out.println("  " + metricsHandler);

        System.out.println("Step 6: Demonstrating Undo/Redo in workflow...");
        CommandExecutor executor = trie.getCommandExecutor();
        System.out.println("  Current size: " + trie.getSize());
        System.out.println("  Can undo: " + executor.getHistory().canUndo());
        System.out.println("  Can redo: " + executor.getHistory().canRedo());

        executor.undo(trie);
        System.out.println("  After undo size: " + trie.getSize());

        executor.redo(trie);
        System.out.println("  After redo size: " + trie.getSize());

        System.out.println("\nStep 7: Architecture summary:");
        System.out.println("  - Event Sourcing: Enabled");
        System.out.println("  - Command Pattern: Enabled with Undo/Redo");
        System.out.println("  - Service Layer: " + trie.getServiceContainer().getRegistry().getServiceCount() + " services");
        System.out.println("  - Interceptors: Command execution timing");
        System.out.println("  - Thread-Safe: Yes (ReadWriteLock)");
        System.out.println("  - Event Bus: Active with metrics collection");
    }
}
