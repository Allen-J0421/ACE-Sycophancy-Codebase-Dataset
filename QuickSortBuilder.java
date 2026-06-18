/**
 * Fluent builder API for creating configured sorters with validation.
 * Provides a clean, chainable API for sorter instantiation.
 *
 * Example:
 *   Sorter<Integer> sorter = new QuickSortBuilder<Integer>()
 *       .algorithm(SorterFactory.Algorithm.INTROSORT)
 *       .insertionThreshold(12)
 *       .enableMetrics()
 *       .build();
 */
class QuickSortBuilder<T extends Comparable<T>> {
    private SorterFactory.Algorithm algorithm = SorterFactory.Algorithm.INTROSORT;
    private SortingConfiguration config = new SortingConfiguration();

    /**
     * Sets the sorting algorithm to use.
     */
    public QuickSortBuilder<T> algorithm(SorterFactory.Algorithm algorithm) {
        if (algorithm == null) {
            throw new SortingException(
                SortingException.ErrorType.INVALID_CONFIGURATION,
                "Algorithm cannot be null"
            );
        }
        this.algorithm = algorithm;
        return this;
    }

    /**
     * Sets the insertion sort threshold.
     */
    public QuickSortBuilder<T> insertionThreshold(int threshold) {
        this.config.setInsertionSortThreshold(threshold);
        return this;
    }

    /**
     * Sets the IntroSort maximum depth.
     */
    public QuickSortBuilder<T> introSortDepth(int depth) {
        this.config.setIntroSortMaxDepth(depth);
        return this;
    }

    /**
     * Enables performance metrics tracking.
     */
    public QuickSortBuilder<T> enableMetrics() {
        this.config.enableMetrics(true);
        return this;
    }

    /**
     * Sets the random seed for randomized algorithms.
     */
    public QuickSortBuilder<T> randomSeed(int seed) {
        this.config.setRandomSeed(seed);
        return this;
    }

    /**
     * Uses the provided configuration.
     */
    public QuickSortBuilder<T> configuration(SortingConfiguration configuration) {
        if (configuration == null) {
            throw new SortingException(
                SortingException.ErrorType.INVALID_CONFIGURATION,
                "Configuration cannot be null"
            );
        }
        InputValidator.validateConfiguration(configuration);
        this.config = configuration;
        return this;
    }

    /**
     * Builds and returns the configured sorter.
     */
    public Sorter<T> build() {
        InputValidator.validateConfiguration(config);
        SorterFactory.setDefaultConfiguration(config);
        return SorterFactory.create(algorithm, config);
    }

    /**
     * Convenience method for creating IntroSort with default settings.
     */
    public static <T extends Comparable<T>> Sorter<T> defaultIntroSort() {
        return new QuickSortBuilder<T>().build();
    }

    /**
     * Convenience method for creating HybridQuickSort with default settings.
     */
    public static <T extends Comparable<T>> Sorter<T> defaultHybrid() {
        return new QuickSortBuilder<T>()
            .algorithm(SorterFactory.Algorithm.QUICKSORT_HYBRID)
            .build();
    }
}
