/**
 * Factory for creating commonly configured PrefixSum instances.
 */
public class PrefixSumFactory {

    /**
     * Creates a simple PrefixSum with default configuration.
     *
     * @return a new PrefixSum instance
     */
    public static PrefixSum createSimple() {
        return new PrefixSum();
    }

    /**
     * Creates a cached PrefixSum that retains results in memory.
     *
     * @return a new cached PrefixSum instance
     */
    public static PrefixSum createCached() {
        return new PrefixSum(PrefixSumConfig.builder().withCache().build());
    }

    /**
     * Creates a cached PrefixSum that clears cache on each computation.
     *
     * @return a new PrefixSum instance with cache clear enabled
     */
    public static PrefixSum createCachedWithClear() {
        return new PrefixSum(
            PrefixSumConfig.builder()
                .withCache()
                .withCacheClear()
                .build()
        );
    }

    /**
     * Creates a monitored PrefixSum that tracks performance metrics.
     *
     * @return a new PrefixSum instance with metrics tracking
     */
    public static PrefixSum createMonitored() {
        PrefixSum calculator = new PrefixSum(
            PrefixSumConfig.builder().withMetrics().build()
        );
        calculator.addListener(new LoggingListener());
        return calculator;
    }

    /**
     * Creates a fully featured PrefixSum with caching, metrics, and logging.
     *
     * @return a new PrefixSum instance with all features enabled
     */
    public static PrefixSum createFull() {
        PrefixSum calculator = new PrefixSum(
            PrefixSumConfig.builder()
                .withCache()
                .withCacheClear()
                .withMetrics()
                .build()
        );
        calculator.addListener(new LoggingListener());
        return calculator;
    }

    /**
     * Creates a PrefixSum with custom strategy and logging.
     *
     * @param strategy the computation strategy
     * @return a new PrefixSum instance with the given strategy
     */
    public static PrefixSum createWithStrategy(ComputationStrategy strategy) {
        PrefixSum calculator = new PrefixSum(
            PrefixSumConfig.builder().withMetrics().build(),
            strategy
        );
        calculator.addListener(new LoggingListener());
        return calculator;
    }
}
