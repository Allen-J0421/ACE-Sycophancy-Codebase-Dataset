/**
 * Dependency Injection Container for managing component lifecycle and instantiation.
 * Provides centralized service resolution with support for singletons and transients.
 *
 * Example usage:
 *   DIContainer container = new DIContainer();
 *   container.registerSorters();
 *   Sorter<Integer> sorter = container.getSorter(
 *       SorterFactory.Algorithm.INTROSORT
 *   );
 */
class DIContainer {
    private final ServiceRegistry registry = new ServiceRegistry();
    private final ServiceResolver resolver;

    public DIContainer() {
        this.resolver = new ServiceResolver(registry);
    }

    /**
     * Registers all default sorter implementations.
     */
    public void registerSorters() {
        registry.registerSingleton(
            "QUICKSORT_CLASSIC",
            Sorter.class,
            () -> new QuickSortImpl<>()
        );

        registry.registerSingleton(
            "QUICKSORT_HYBRID",
            Sorter.class,
            () -> new HybridQuickSort<Integer>(new MedianOfThreePivotSelector<Integer>())
        );

        registry.registerSingleton(
            "QUICKSORT_THREEWAY",
            Sorter.class,
            () -> new ThreeWayQuickSort<>()
        );

        registry.registerSingleton(
            "HEAPSORT",
            Sorter.class,
            () -> new HeapSort<>()
        );

        registry.registerSingleton(
            "INTROSORT",
            Sorter.class,
            () -> new IntroSort<>()
        );
    }

    /**
     * Registers all pivot selector strategies.
     */
    public void registerPivotSelectors() {
        registry.registerSingleton(
            "MedianOfThree",
            PivotSelector.class,
            () -> new MedianOfThreePivotSelector<>()
        );

        registry.registerSingleton(
            "Random",
            PivotSelector.class,
            () -> new RandomPivotSelector<>()
        );
    }

    /**
     * Registers configuration and utilities.
     */
    public void registerUtilities() {
        registry.registerTransient(
            "Configuration",
            SortingConfiguration.class,
            () -> new SortingConfiguration()
        );

        registry.registerTransient(
            "Metrics",
            SortingMetrics.class,
            () -> new SortingMetrics()
        );
    }

    /**
     * Registers all default services.
     */
    public void registerDefaults() {
        registerSorters();
        registerPivotSelectors();
        registerUtilities();
    }

    /**
     * Gets a sorter by algorithm type.
     */
    @SuppressWarnings("unchecked")
    public <T extends Comparable<T>> Sorter<T> getSorter(SorterFactory.Algorithm algorithm) {
        String serviceName = algorithm.name();
        return (Sorter<T>) resolver.resolve(serviceName);
    }

    /**
     * Gets a pivot selector by name.
     */
    @SuppressWarnings("unchecked")
    public <T extends Comparable<T>> PivotSelector<T> getPivotSelector(String name) {
        return (PivotSelector<T>) resolver.resolve(name);
    }

    /**
     * Gets configuration instance.
     */
    public SortingConfiguration getConfiguration() {
        return resolver.resolve("Configuration");
    }

    /**
     * Gets metrics instance.
     */
    public SortingMetrics getMetrics() {
        return resolver.resolve("Metrics");
    }

    /**
     * Registers a custom service.
     */
    public <T> void register(String name, Class<T> type, ServiceRegistry.ServiceFactory<T> factory, boolean singleton) {
        if (singleton) {
            registry.registerSingleton(name, type, factory);
        } else {
            registry.registerTransient(name, type, factory);
        }
    }

    /**
     * Gets underlying service registry.
     */
    public ServiceRegistry getRegistry() {
        return registry;
    }

    /**
     * Gets underlying service resolver.
     */
    public ServiceResolver getResolver() {
        return resolver;
    }

    /**
     * Clears all registered services.
     */
    public void clear() {
        registry.clear();
    }
}
