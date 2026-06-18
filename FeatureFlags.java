import java.util.HashMap;
import java.util.Map;

public class FeatureFlags {
    private static final FeatureFlags instance = new FeatureFlags();
    private final Map<String, Boolean> flags = new java.util.concurrent.ConcurrentHashMap<>();

    private FeatureFlags() {
        initializeDefaults();
    }

    public static FeatureFlags getInstance() {
        return instance;
    }

    private void initializeDefaults() {
        flags.put("cache_enabled", true);
        flags.put("tracing_enabled", true);
        flags.put("audit_logging_enabled", true);
        flags.put("rate_limiting_enabled", true);
        flags.put("validation_enabled", true);
        flags.put("optimization_enabled", false);
    }

    public void enableFeature(String featureName) {
        flags.put(featureName, true);
        Logger.info("Feature enabled: " + featureName);
    }

    public void disableFeature(String featureName) {
        flags.put(featureName, false);
        Logger.info("Feature disabled: " + featureName);
    }

    public boolean isEnabled(String featureName) {
        return flags.getOrDefault(featureName, false);
    }

    public void setFeature(String featureName, boolean enabled) {
        flags.put(featureName, enabled);
    }

    public Map<String, Boolean> getAllFlags() {
        return new HashMap<>(flags);
    }

    public void reset() {
        flags.clear();
        initializeDefaults();
        Logger.info("Feature flags reset to defaults");
    }
}
