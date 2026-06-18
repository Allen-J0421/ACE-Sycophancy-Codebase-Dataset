import java.util.*;

public class AdvancedDemo {
    public static void main(String[] args) {
        demonstrateEventSourcing();
        demonstrateCommandPattern();
        demonstrateUndoRedo();
        demonstrateServiceLayer();
        demonstrateEventBusMetrics();
        demonstrateCommandInterceptors();
    }

    private static void demonstrateEventSourcing() {
        System.out.println("=== Event Sourcing Demo ===");
        AdvancedTrie trie = new AdvancedTrie();
        EventBus eventBus = trie.getEventBus();

        LoggingEventHandler loggingHandler = new LoggingEventHandler("EventLog");
        eventBus.subscribe(loggingHandler);

        trie.insert("hello");
        trie.insert("world");
        trie.search("hello");
        trie.delete("hello");

        System.out.println("Event log size: " + eventBus.getEventLogSize());
    }

    private static void demonstrateCommandPattern() {
        System.out.println("\n=== Command Pattern Demo ===");
        AdvancedTrie trie = new AdvancedTrie();

        TrieCommand insertCmd = new InsertWordCommand("apple");
        OperationResult<?> result = trie.executeCommand(insertCmd);
        System.out.println("Executed: " + insertCmd.getCommandName() + " -> " + result.getMessage());

        TrieCommand searchCmd = new SearchWordCommand("apple");
        OperationResult<?> searchResult = trie.executeCommand(searchCmd);
        System.out.println("Executed: " + searchCmd.getCommandName() + " -> Found: " + searchResult.getValue());

        TrieCommand deleteCmd = new DeleteWordCommand("apple");
        OperationResult<?> deleteResult = trie.executeCommand(deleteCmd);
        System.out.println("Executed: " + deleteCmd.getCommandName() + " -> " + deleteResult.getMessage());
    }

    private static void demonstrateUndoRedo() {
        System.out.println("\n=== Undo/Redo Demo ===");
        AdvancedTrie trie = new AdvancedTrie();
        CommandExecutor executor = trie.getCommandExecutor();

        System.out.println("Inserting words...");
        trie.executeCommand(new InsertWordCommand("undo"));
        trie.executeCommand(new InsertWordCommand("redo"));
        trie.executeCommand(new InsertWordCommand("test"));
        System.out.println("Current words: " + trie.getAllWords());

        System.out.println("\nUndoing last operation...");
        if (executor.undo(trie)) {
            System.out.println("Words after undo: " + trie.getAllWords());
        }

        System.out.println("\nRedoing operation...");
        if (executor.redo(trie)) {
            System.out.println("Words after redo: " + trie.getAllWords());
        }

        System.out.println("Undo stack size: " + executor.getHistory().getUndoStackSize());
        System.out.println("Redo stack size: " + executor.getHistory().getRedoStackSize());
    }

    private static void demonstrateServiceLayer() {
        System.out.println("\n=== Service Layer Demo ===");
        AdvancedTrie trie = new AdvancedTrie();

        System.out.println("Inserting words via command service...");
        trie.executeCommand(new InsertWordCommand("service"));
        trie.executeCommand(new InsertWordCommand("layer"));
        trie.executeCommand(new InsertWordCommand("architecture"));

        System.out.println("\nUsing search service...");
        SearchService searchService = trie.getServiceContainer().getRegistry().getService(SearchService.class);
        System.out.println("Search 'service': " + searchService.search("service", trie));
        System.out.println("Find by prefix 'ser': " + searchService.findByPrefix("ser", trie));

        System.out.println("\nUsing analytics service...");
        AnalyticsService analyticsService = trie.getServiceContainer().getRegistry().getService(AnalyticsService.class);
        Map<String, Object> analytics = analyticsService.getAnalytics(trie);
        analytics.forEach((key, value) -> System.out.println("  " + key + ": " + value));

        System.out.println("\nRegistered services: " + trie.getServiceContainer().getRegistry().getServiceCount());
    }

    private static void demonstrateEventBusMetrics() {
        System.out.println("\n=== Event Bus Metrics Demo ===");
        AdvancedTrie trie = new AdvancedTrie();
        EventBus eventBus = trie.getEventBus();

        MetricsEventHandler metricsHandler = new MetricsEventHandler();
        eventBus.subscribe(metricsHandler);

        System.out.println("Performing operations...");
        trie.insert("metrics");
        trie.insert("event");
        trie.insert("bus");
        for (int i = 0; i < 5; i++) {
            trie.search("metrics");
        }
        trie.search("missing");
        trie.delete("event");

        System.out.println("Event-based metrics: " + metricsHandler);
        System.out.println("Total events: " + eventBus.getEventLogSize());

        System.out.println("\nEvent log entries:");
        eventBus.getEventLog().stream()
            .limit(5)
            .forEach(e -> System.out.println("  " + e));
    }

    private static void demonstrateCommandInterceptors() {
        System.out.println("\n=== Command Interceptors Demo ===");
        AdvancedTrie trie = new AdvancedTrie();
        CommandExecutor executor = trie.getCommandExecutor();

        TimingInterceptor timingInterceptor = new TimingInterceptor();
        executor.addInterceptor(timingInterceptor);

        System.out.println("Executing commands with timing interceptor...");
        trie.executeCommand(new InsertWordCommand("interceptor"));
        trie.executeCommand(new InsertWordCommand("timing"));
        trie.executeCommand(new SearchWordCommand("interceptor"));

        System.out.println("\nCommand execution times:");
        timingInterceptor.getExecutionTimes().forEach((cmd, time) ->
            System.out.println("  " + cmd + ": " + time + "ms")
        );

        System.out.println("\nUndo/Redo capability:");
        System.out.println("  Can undo: " + executor.getHistory().canUndo());
        System.out.println("  Can redo: " + executor.getHistory().canRedo());
    }
}
