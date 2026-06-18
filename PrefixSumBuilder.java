/**
 * Fluent builder for creating configured PrefixSum instances.
 */
public class PrefixSumBuilder {
    private PrefixSumConfig config;
    private ComputationStrategy strategy = new IterativeStrategy();
    private final CompositeListener listeners = new CompositeListener();

    public PrefixSumBuilder() {
        this.config = PrefixSumConfig.defaults();
    }

    public PrefixSumBuilder withCaching() {
        this.config = PrefixSumConfig.builder()
            .withCache()
            .build();
        return this;
    }

    public PrefixSumBuilder withCacheClear() {
        this.config = PrefixSumConfig.builder()
            .withCache()
            .withCacheClear()
            .build();
        return this;
    }

    public PrefixSumBuilder withMetrics() {
        this.config = PrefixSumConfig.builder()
            .withMetrics()
            .build();
        return this;
    }

    public PrefixSumBuilder withAllFeatures() {
        this.config = PrefixSumConfig.builder()
            .withCache()
            .withCacheClear()
            .withMetrics()
            .build();
        return this;
    }

    public PrefixSumBuilder withStrategy(ComputationStrategy strategy) {
        this.strategy = strategy;
        return this;
    }

    public PrefixSumBuilder withLogging() {
        listeners.add(new LoggingListener());
        return this;
    }

    public PrefixSumBuilder withTiming() {
        listeners.add(new TimingListener());
        return this;
    }

    public PrefixSumBuilder withListener(ComputationListener listener) {
        listeners.add(listener);
        return this;
    }

    public PrefixSumBuilder withLambdaListener(LambdaListener listener) {
        listeners.add(listener);
        return this;
    }

    public PrefixSum build() {
        PrefixSum calculator = new PrefixSum(config, strategy);
        if (!listeners.hasListeners()) {
            // Only add the composite if it has listeners
            calculator.addListener(listeners);
        } else {
            calculator.addListener(listeners);
        }
        return calculator;
    }
}
