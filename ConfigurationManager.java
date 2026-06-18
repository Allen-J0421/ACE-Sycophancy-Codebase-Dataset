import java.util.HashMap;
import java.util.Map;

public class ConfigurationManager {
    private final Map<String, Object> config = new HashMap<>();
    private static final ConfigurationManager instance = new ConfigurationManager();

    private ConfigurationManager() {
        initializeDefaults();
    }

    public static ConfigurationManager getInstance() {
        return instance;
    }

    private void initializeDefaults() {
        config.put("cache.enabled", true);
        config.put("cache.ttl.millis", 300000);
        config.put("logging.level", "INFO");
        config.put("logging.enabled", true);
        config.put("metrics.enabled", true);
        config.put("health.check.enabled", true);
        config.put("benchmark.iterations", 100);
        config.put("concurrent.threads", 4);
        config.put("validation.enabled", true);
        config.put("serialization.format", "JSON");
    }

    public Object get(String key) {
        return config.get(key);
    }

    public Object get(String key, Object defaultValue) {
        return config.getOrDefault(key, defaultValue);
    }

    public void set(String key, Object value) {
        config.put(key, value);
        Logger.debug("Configuration updated: " + key + " = " + value);
    }

    public int getInt(String key, int defaultValue) {
        Object value = config.get(key);
        return value instanceof Integer ? (Integer) value : defaultValue;
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        Object value = config.get(key);
        return value instanceof Boolean ? (Boolean) value : defaultValue;
    }

    public String getString(String key, String defaultValue) {
        Object value = config.get(key);
        return value instanceof String ? (String) value : defaultValue;
    }

    public long getLong(String key, long defaultValue) {
        Object value = config.get(key);
        return value instanceof Long ? (Long) value : defaultValue;
    }

    public void loadFromMap(Map<String, Object> properties) {
        config.putAll(properties);
        Logger.info("Configuration loaded from map");
    }

    public Map<String, Object> getAll() {
        return new HashMap<>(config);
    }

    public void reset() {
        config.clear();
        initializeDefaults();
        Logger.info("Configuration reset to defaults");
    }
}
