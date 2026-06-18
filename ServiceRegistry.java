import java.util.HashMap;
import java.util.Map;

/**
 * Service registry for managing component definitions.
 * Holds factory functions and lifecycle information for registered services.
 */
class ServiceRegistry {
    private final Map<String, ServiceDefinition<?>> definitions = new HashMap<>();

    /**
     * Registers a singleton service.
     */
    public <T> void registerSingleton(String name, Class<T> type, ServiceFactory<T> factory) {
        definitions.put(name, new ServiceDefinition<>(name, type, factory, Lifecycle.SINGLETON));
    }

    /**
     * Registers a transient service (new instance each time).
     */
    public <T> void registerTransient(String name, Class<T> type, ServiceFactory<T> factory) {
        definitions.put(name, new ServiceDefinition<>(name, type, factory, Lifecycle.TRANSIENT));
    }

    /**
     * Gets a service definition by name.
     */
    public ServiceDefinition<?> getDefinition(String name) {
        ServiceDefinition<?> def = definitions.get(name);
        if (def == null) {
            throw new SortingException(
                SortingException.ErrorType.INVALID_CONFIGURATION,
                "Service not registered: " + name
            );
        }
        return def;
    }

    /**
     * Checks if a service is registered.
     */
    public boolean contains(String name) {
        return definitions.containsKey(name);
    }

    /**
     * Gets all registered service names.
     */
    public Iterable<String> getServiceNames() {
        return definitions.keySet();
    }

    /**
     * Clears all registrations.
     */
    public void clear() {
        definitions.clear();
    }

    /**
     * Service lifecycle enumeration.
     */
    enum Lifecycle {
        SINGLETON("Singleton - one instance for application lifetime"),
        TRANSIENT("Transient - new instance each time");

        private final String description;

        Lifecycle(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * Service factory interface for creating service instances.
     */
    @FunctionalInterface
    interface ServiceFactory<T> {
        T create();
    }

    /**
     * Service definition holding metadata and factory.
     */
    static class ServiceDefinition<T> {
        private final String name;
        private final Class<T> type;
        private final ServiceFactory<T> factory;
        private final Lifecycle lifecycle;
        private T singletonInstance;

        ServiceDefinition(String name, Class<T> type, ServiceFactory<T> factory, Lifecycle lifecycle) {
            this.name = name;
            this.type = type;
            this.factory = factory;
            this.lifecycle = lifecycle;
        }

        public String getName() {
            return name;
        }

        public Class<T> getType() {
            return type;
        }

        public ServiceFactory<T> getFactory() {
            return factory;
        }

        public Lifecycle getLifecycle() {
            return lifecycle;
        }

        public T getSingletonInstance() {
            return singletonInstance;
        }

        public void setSingletonInstance(T instance) {
            this.singletonInstance = instance;
        }

        public boolean isSingleton() {
            return lifecycle == Lifecycle.SINGLETON;
        }
    }
}
