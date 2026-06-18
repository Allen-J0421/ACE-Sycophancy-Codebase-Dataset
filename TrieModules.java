import java.util.*;

// Module system for composing Trie functionality

interface TrieModule {
    String getName();
    void initialize(Trie trie);
    void shutdown();
    ModuleStatus getStatus();
}

class ModuleStatus {
    enum Status {
        UNINITIALIZED, INITIALIZING, ACTIVE, DEGRADED, FAILED, SHUTDOWN
    }

    private final Status status;
    private final String message;
    private final long timestamp;

    ModuleStatus(Status status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }

    Status getStatus() {
        return status;
    }

    String getMessage() {
        return message;
    }

    long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return String.format("ModuleStatus{%s: %s}", status, message);
    }
}

// Module implementations

class CachingModule implements TrieModule {
    private CachedTrieDecorator cachedTrie;
    private ModuleStatus status;

    @Override
    public String getName() {
        return "CachingModule";
    }

    @Override
    public void initialize(Trie trie) {
        try {
            cachedTrie = TrieDecoratorFactory.withCaching(trie, 512);
            status = new ModuleStatus(ModuleStatus.Status.ACTIVE, "Caching module initialized");
            System.out.println("[" + getName() + "] Initialized successfully");
        } catch (Exception e) {
            status = new ModuleStatus(ModuleStatus.Status.FAILED, "Initialization failed: " + e.getMessage());
        }
    }

    @Override
    public void shutdown() {
        status = new ModuleStatus(ModuleStatus.Status.SHUTDOWN, "Caching module shut down");
    }

    @Override
    public ModuleStatus getStatus() {
        return status;
    }

    CachedTrieDecorator getCachedTrie() {
        return cachedTrie;
    }
}

class LoggingModule implements TrieModule {
    private LoggingTrieDecorator loggingTrie;
    private ModuleStatus status;

    @Override
    public String getName() {
        return "LoggingModule";
    }

    @Override
    public void initialize(Trie trie) {
        try {
            loggingTrie = TrieDecoratorFactory.withLogging(trie, "TrieSystem");
            status = new ModuleStatus(ModuleStatus.Status.ACTIVE, "Logging module initialized");
            System.out.println("[" + getName() + "] Initialized successfully");
        } catch (Exception e) {
            status = new ModuleStatus(ModuleStatus.Status.FAILED, "Initialization failed: " + e.getMessage());
        }
    }

    @Override
    public void shutdown() {
        status = new ModuleStatus(ModuleStatus.Status.SHUTDOWN, "Logging module shut down");
    }

    @Override
    public ModuleStatus getStatus() {
        return status;
    }

    LoggingTrieDecorator getLoggingTrie() {
        return loggingTrie;
    }
}

class HealthCheckModule implements TrieModule {
    private HealthMonitor monitor;
    private ModuleStatus status;

    @Override
    public String getName() {
        return "HealthCheckModule";
    }

    @Override
    public void initialize(Trie trie) {
        try {
            monitor = new HealthMonitor(trie);
            status = new ModuleStatus(ModuleStatus.Status.ACTIVE, "Health check module initialized");
            System.out.println("[" + getName() + "] Initialized successfully");
        } catch (Exception e) {
            status = new ModuleStatus(ModuleStatus.Status.FAILED, "Initialization failed: " + e.getMessage());
        }
    }

    @Override
    public void shutdown() {
        if (monitor != null) {
            monitor.stopMonitoring();
        }
        status = new ModuleStatus(ModuleStatus.Status.SHUTDOWN, "Health check module shut down");
    }

    @Override
    public ModuleStatus getStatus() {
        return status;
    }

    HealthMonitor getMonitor() {
        return monitor;
    }
}

class MetricsModule implements TrieModule {
    private StatisticsTrieDecorator statisticsTrie;
    private ModuleStatus status;

    @Override
    public String getName() {
        return "MetricsModule";
    }

    @Override
    public void initialize(Trie trie) {
        try {
            statisticsTrie = TrieDecoratorFactory.withStatistics(trie);
            status = new ModuleStatus(ModuleStatus.Status.ACTIVE, "Metrics module initialized");
            System.out.println("[" + getName() + "] Initialized successfully");
        } catch (Exception e) {
            status = new ModuleStatus(ModuleStatus.Status.FAILED, "Initialization failed: " + e.getMessage());
        }
    }

    @Override
    public void shutdown() {
        status = new ModuleStatus(ModuleStatus.Status.SHUTDOWN, "Metrics module shut down");
    }

    @Override
    public ModuleStatus getStatus() {
        return status;
    }

    StatisticsTrieDecorator getStatisticsTrie() {
        return statisticsTrie;
    }
}

// Module manager for composition

class ModuleManager {
    private final Trie trie;
    private final Map<String, TrieModule> modules = new LinkedHashMap<>();
    private final List<TrieModule> initializationOrder = new ArrayList<>();

    ModuleManager(Trie trie) {
        this.trie = trie;
    }

    void registerModule(TrieModule module) {
        modules.put(module.getName(), module);
        initializationOrder.add(module);
    }

    void initializeAll() {
        System.out.println("\n=== Module Initialization ===");
        for (TrieModule module : initializationOrder) {
            module.initialize(trie);
        }
        System.out.println("All modules initialized");
    }

    void shutdownAll() {
        System.out.println("\n=== Module Shutdown ===");
        for (int i = initializationOrder.size() - 1; i >= 0; i--) {
            initializationOrder.get(i).shutdown();
        }
        System.out.println("All modules shut down");
    }

    TrieModule getModule(String name) {
        return modules.get(name);
    }

    Map<String, ModuleStatus> getModuleStatuses() {
        Map<String, ModuleStatus> statuses = new LinkedHashMap<>();
        for (TrieModule module : modules.values()) {
            statuses.put(module.getName(), module.getStatus());
        }
        return statuses;
    }

    void printModuleStatus() {
        System.out.println("\n=== Module Status ===");
        for (Map.Entry<String, ModuleStatus> entry : getModuleStatuses().entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }

    int getModuleCount() {
        return modules.size();
    }
}
