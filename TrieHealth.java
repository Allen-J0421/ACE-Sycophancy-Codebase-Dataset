import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

// Health check system for monitoring and resilience

interface HealthCheck {
    HealthStatus check(Trie trie);
}

class HealthStatus {
    enum Status {
        HEALTHY("System is operating normally"),
        WARNING("System has warnings but operational"),
        UNHEALTHY("System is not operating correctly"),
        CRITICAL("System requires immediate attention");

        private final String description;

        Status(String description) {
            this.description = description;
        }

        String getDescription() {
            return description;
        }
    }

    private final Status status;
    private final String message;
    private final long timestamp;
    private final Map<String, Object> details;

    HealthStatus(Status status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
        this.details = new LinkedHashMap<>();
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

    void addDetail(String key, Object value) {
        details.put(key, value);
    }

    Map<String, Object> getDetails() {
        return new LinkedHashMap<>(details);
    }

    @Override
    public String toString() {
        return String.format("HealthStatus{status=%s, message=%s, details=%s}",
            status, message, details);
    }
}

// Health check implementations

class MemoryHealthCheck implements HealthCheck {
    private final long maxMemoryMB;

    MemoryHealthCheck(long maxMemoryMB) {
        this.maxMemoryMB = maxMemoryMB;
    }

    @Override
    public HealthStatus check(Trie trie) {
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        long usedMB = usedMemory / 1_000_000;

        if (usedMB > maxMemoryMB) {
            HealthStatus status = new HealthStatus(HealthStatus.Status.WARNING,
                "High memory usage: " + usedMB + "MB");
            status.addDetail("usedMemoryMB", usedMB);
            status.addDetail("maxMemoryMB", maxMemoryMB);
            return status;
        }

        HealthStatus status = new HealthStatus(HealthStatus.Status.HEALTHY,
            "Memory usage normal: " + usedMB + "MB");
        status.addDetail("usedMemoryMB", usedMB);
        return status;
    }
}

class SizeHealthCheck implements HealthCheck {
    private final int maxSize;

    SizeHealthCheck(int maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    public HealthStatus check(Trie trie) {
        int size = trie.getSize();

        if (size > maxSize) {
            HealthStatus status = new HealthStatus(HealthStatus.Status.WARNING,
                "Trie size exceeds threshold: " + size + "/" + maxSize);
            status.addDetail("currentSize", size);
            status.addDetail("maxSize", maxSize);
            status.addDetail("percentFull", (size * 100) / maxSize);
            return status;
        }

        HealthStatus status = new HealthStatus(HealthStatus.Status.HEALTHY,
            "Trie size within limits: " + size);
        status.addDetail("currentSize", size);
        status.addDetail("maxSize", maxSize);
        return status;
    }
}

class StateHealthCheck implements HealthCheck {
    @Override
    public HealthStatus check(Trie trie) {
        if (trie instanceof AdvancedTrie) {
            AdvancedTrie advancedTrie = (AdvancedTrie) trie;
            TrieState state = advancedTrie.getState();

            if (state == TrieState.FROZEN) {
                HealthStatus status = new HealthStatus(HealthStatus.Status.HEALTHY,
                    "Trie is frozen and immutable");
                status.addDetail("state", state.getDescription());
                return status;
            }

            HealthStatus status = new HealthStatus(HealthStatus.Status.HEALTHY,
                "Trie state is normal");
            status.addDetail("state", state.getDescription());
            return status;
        }

        return new HealthStatus(HealthStatus.Status.HEALTHY, "Trie is operational");
    }
}

class PerformanceHealthCheck implements HealthCheck {
    private final long maxExecutionTimeMs;

    PerformanceHealthCheck(long maxExecutionTimeMs) {
        this.maxExecutionTimeMs = maxExecutionTimeMs;
    }

    @Override
    public HealthStatus check(Trie trie) {
        TrieStatistics stats = trie.getStatistics();
        int depth = stats.getMaxDepth();

        if (depth > 100) {
            HealthStatus status = new HealthStatus(HealthStatus.Status.WARNING,
                "Trie depth is high, search performance may degrade: " + depth);
            status.addDetail("maxDepth", depth);
            status.addDetail("totalNodes", stats.getTotalNodes());
            return status;
        }

        HealthStatus status = new HealthStatus(HealthStatus.Status.HEALTHY,
            "Performance metrics normal");
        status.addDetail("maxDepth", depth);
        status.addDetail("totalWords", stats.getTotalWords());
        return status;
    }
}

// Health check registry
class HealthCheckRegistry {
    private final List<HealthCheck> checks = new CopyOnWriteArrayList<>();
    private final Map<String, HealthStatus> lastResults = new LinkedHashMap<>();
    private final ReadWriteLock lock = new java.util.concurrent.locks.ReentrantReadWriteLock();

    void registerCheck(String name, HealthCheck check) {
        checks.add(check);
        lastResults.put(name, new HealthStatus(HealthStatus.Status.HEALTHY, "Not yet executed"));
    }

    HealthStatus performCheck(String name, HealthCheck check, Trie trie) {
        HealthStatus status = check.check(trie);
        lock.writeLock().lock();
        try {
            lastResults.put(name, status);
        } finally {
            lock.writeLock().unlock();
        }
        return status;
    }

    Map<String, HealthStatus> performAllChecks(Trie trie) {
        Map<String, HealthStatus> results = new LinkedHashMap<>();
        List<String> names = new ArrayList<>(lastResults.keySet());

        for (int i = 0; i < names.size() && i < checks.size(); i++) {
            String name = names.get(i);
            HealthCheck check = checks.get(i);
            results.put(name, performCheck(name, check, trie));
        }

        return results;
    }

    HealthStatus.Status getOverallStatus(Map<String, HealthStatus> results) {
        if (results.isEmpty()) {
            return HealthStatus.Status.HEALTHY;
        }

        for (HealthStatus status : results.values()) {
            if (status.getStatus() == HealthStatus.Status.CRITICAL) {
                return HealthStatus.Status.CRITICAL;
            }
        }

        for (HealthStatus status : results.values()) {
            if (status.getStatus() == HealthStatus.Status.UNHEALTHY) {
                return HealthStatus.Status.UNHEALTHY;
            }
        }

        for (HealthStatus status : results.values()) {
            if (status.getStatus() == HealthStatus.Status.WARNING) {
                return HealthStatus.Status.WARNING;
            }
        }

        return HealthStatus.Status.HEALTHY;
    }

    Map<String, HealthStatus> getLastResults() {
        lock.readLock().lock();
        try {
            return new LinkedHashMap<>(lastResults);
        } finally {
            lock.readLock().unlock();
        }
    }
}

// Health monitor with periodic checks
class HealthMonitor {
    private final Trie trie;
    private final HealthCheckRegistry registry;
    private volatile boolean monitoring;

    HealthMonitor(Trie trie) {
        this.trie = trie;
        this.registry = new HealthCheckRegistry();
        this.monitoring = false;
        initializeDefaultChecks();
    }

    private void initializeDefaultChecks() {
        registry.registerCheck("Memory", new MemoryHealthCheck(512));
        registry.registerCheck("Size", new SizeHealthCheck(10000));
        registry.registerCheck("State", new StateHealthCheck());
        registry.registerCheck("Performance", new PerformanceHealthCheck(5000));
    }

    Map<String, HealthStatus> checkHealth() {
        return registry.performAllChecks(trie);
    }

    HealthStatus.Status getSystemStatus() {
        Map<String, HealthStatus> results = checkHealth();
        return registry.getOverallStatus(results);
    }

    void startMonitoring(long intervalMs) {
        monitoring = true;
        new Thread(() -> {
            while (monitoring) {
                try {
                    Map<String, HealthStatus> results = checkHealth();
                    HealthStatus.Status overall = registry.getOverallStatus(results);
                    System.out.println("[HealthMonitor] Overall Status: " + overall.getDescription());
                    Thread.sleep(intervalMs);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }, "TrieHealthMonitor").start();
    }

    void stopMonitoring() {
        monitoring = false;
    }

    HealthCheckRegistry getRegistry() {
        return registry;
    }
}
