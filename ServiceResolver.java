/**
 * Service resolver for instantiating and managing service lifecycle.
 * Handles singleton caching and transient creation.
 */
class ServiceResolver {
    private final ServiceRegistry registry;

    public ServiceResolver(ServiceRegistry registry) {
        this.registry = registry;
    }

    /**
     * Resolves and returns a service instance.
     * For singletons, returns cached instance.
     * For transients, creates new instance each time.
     */
    @SuppressWarnings("unchecked")
    public <T> T resolve(String name) {
        ServiceRegistry.ServiceDefinition<T> definition =
            (ServiceRegistry.ServiceDefinition<T>) registry.getDefinition(name);

        if (definition.isSingleton()) {
            return resolveSingleton(definition);
        } else {
            return resolveTransient(definition);
        }
    }

    /**
     * Resolves a singleton service, using cache if available.
     */
    private <T> T resolveSingleton(ServiceRegistry.ServiceDefinition<T> definition) {
        T instance = definition.getSingletonInstance();

        if (instance == null) {
            instance = definition.getFactory().create();
            definition.setSingletonInstance(instance);
        }

        return instance;
    }

    /**
     * Resolves a transient service, creating new instance each time.
     */
    private <T> T resolveTransient(ServiceRegistry.ServiceDefinition<T> definition) {
        return definition.getFactory().create();
    }

    /**
     * Gets service registry.
     */
    public ServiceRegistry getRegistry() {
        return registry;
    }

    /**
     * Clears all singleton caches.
     */
    public void clearSingletonCache() {
        for (String name : registry.getServiceNames()) {
            ServiceRegistry.ServiceDefinition<?> def = registry.getDefinition(name);
            if (def.isSingleton()) {
                def.setSingletonInstance(null);
            }
        }
    }
}
