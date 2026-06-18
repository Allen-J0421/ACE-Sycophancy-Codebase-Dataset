import java.util.*;

// Configuration management system

interface TrieConfiguration {
    <T> T get(String key, T defaultValue);
    <T> T get(String key);
    void set(String key, Object value);
    boolean has(String key);
    Map<String, Object> getAll();
}

class MapBasedConfiguration implements TrieConfiguration {
    private final Map<String, Object> values = new LinkedHashMap<>();

    @Override
    public <T> T get(String key, T defaultValue) {
        Object value = values.getOrDefault(key, defaultValue);
        return (T) value;
    }

    @Override
    public <T> T get(String key) {
        Object value = values.get(key);
        if (value == null) {
            throw new TrieException("Configuration key not found: " + key);
        }
        return (T) value;
    }

    @Override
    public void set(String key, Object value) {
        values.put(key, value);
    }

    @Override
    public boolean has(String key) {
        return values.containsKey(key);
    }

    @Override
    public Map<String, Object> getAll() {
        return new LinkedHashMap<>(values);
    }
}

// Default configuration builder
class TrieConfigBuilder {
    private final Map<String, Object> config = new LinkedHashMap<>();

    TrieConfigBuilder withCacheSize(int size) {
        config.put("cache.size", size);
        return this;
    }

    TrieConfigBuilder withMaxEventLogSize(int size) {
        config.put("event.log.maxSize", size);
        return this;
    }

    TrieConfigBuilder withMaxHistorySize(int size) {
        config.put("history.maxSize", size);
        return this;
    }

    TrieConfigBuilder withMaxWordLength(int length) {
        config.put("word.maxLength", length);
        return this;
    }

    TrieConfigBuilder withHealthCheckInterval(long ms) {
        config.put("health.checkInterval", ms);
        return this;
    }

    TrieConfigBuilder withMaxMemoryMB(long mb) {
        config.put("health.maxMemoryMB", mb);
        return this;
    }

    TrieConfigBuilder withMaxTrieSize(int size) {
        config.put("health.maxTrieSize", size);
        return this;
    }

    TrieConfigBuilder withMaxDepth(int depth) {
        config.put("health.maxDepth", depth);
        return this;
    }

    TrieConfigBuilder withEnableMetrics(boolean enabled) {
        config.put("metrics.enabled", enabled);
        return this;
    }

    TrieConfigBuilder withEnableCaching(boolean enabled) {
        config.put("cache.enabled", enabled);
        return this;
    }

    TrieConfigBuilder withEnableEventSourcing(boolean enabled) {
        config.put("events.enabled", enabled);
        return this;
    }

    TrieConfiguration build() {
        MapBasedConfiguration config = new MapBasedConfiguration();

        // Set defaults
        config.set("cache.size", 1024);
        config.set("cache.enabled", true);
        config.set("event.log.maxSize", 10000);
        config.set("events.enabled", true);
        config.set("history.maxSize", 1000);
        config.set("word.maxLength", Integer.MAX_VALUE);
        config.set("health.checkInterval", 5000);
        config.set("health.maxMemoryMB", 512);
        config.set("health.maxTrieSize", 10000);
        config.set("health.maxDepth", 100);
        config.set("metrics.enabled", true);

        // Override with provided values
        this.config.forEach(config::set);

        return config;
    }
}

// Configuration profiles for different environments
class TrieConfigProfile {
    static TrieConfiguration development() {
        return new TrieConfigBuilder()
            .withCacheSize(256)
            .withMaxEventLogSize(5000)
            .withMaxMemoryMB(256)
            .withEnableMetrics(true)
            .build();
    }

    static TrieConfiguration production() {
        return new TrieConfigBuilder()
            .withCacheSize(4096)
            .withMaxEventLogSize(50000)
            .withMaxMemoryMB(1024)
            .withMaxTrieSize(100000)
            .withEnableMetrics(true)
            .build();
    }

    static TrieConfiguration testing() {
        return new TrieConfigBuilder()
            .withCacheSize(128)
            .withMaxEventLogSize(1000)
            .withMaxMemoryMB(128)
            .withMaxTrieSize(1000)
            .withEnableMetrics(false)
            .build();
    }

    static TrieConfiguration minimal() {
        return new TrieConfigBuilder()
            .withCacheSize(64)
            .withMaxEventLogSize(1000)
            .withEnableCaching(false)
            .withEnableEventSourcing(false)
            .withEnableMetrics(false)
            .build();
    }
}
