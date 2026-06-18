import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ServiceContainer {
    private static final ServiceContainer instance = new ServiceContainer();
    private final Map<String, Object> singletons = new HashMap<>();
    private final Map<String, Supplier<?>> factories = new HashMap<>();

    private ServiceContainer() {
        registerDefaults();
    }

    public static ServiceContainer getInstance() {
        return instance;
    }

    private void registerDefaults() {
        register("ConfigurationManager", ConfigurationManager.getInstance());
        register("MetricsCollector", MetricsCollector.getInstance());
        register("AuditLog", AuditLog.getInstance());
        register("DistributedTracer", DistributedTracer.getInstance());
    }

    public <T> void register(String name, T service) {
        singletons.put(name, service);
        Logger.debug("Service registered: " + name);
    }

    public <T> void registerFactory(String name, Supplier<T> factory) {
        factories.put(name, factory);
        Logger.debug("Factory registered: " + name);
    }

    @SuppressWarnings("unchecked")
    public <T> T resolve(String name) {
        Object singleton = singletons.get(name);
        if (singleton != null) {
            return (T) singleton;
        }

        Supplier<?> factory = factories.get(name);
        if (factory != null) {
            return (T) factory.get();
        }

        throw new ServiceNotFoundException("Service not found: " + name);
    }

    public boolean isRegistered(String name) {
        return singletons.containsKey(name) || factories.containsKey(name);
    }

    public static class ServiceNotFoundException extends RuntimeException {
        public ServiceNotFoundException(String message) {
            super(message);
        }
    }
}
