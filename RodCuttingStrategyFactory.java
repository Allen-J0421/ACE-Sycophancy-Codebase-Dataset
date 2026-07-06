class RodCuttingStrategyFactory {
    private final ResultLogger logger;

    RodCuttingStrategyFactory(ResultLogger logger) {
        this.logger = logger;
    }

    RodCuttingStrategy create(SolverType type) {
        return new LoggingDecorator(bareStrategy(type), logger);
    }

    private RodCuttingStrategy bareStrategy(SolverType type) {
        switch (type) {
            case ITERATIVE_DP:       return new IterativeDPStrategy();
            case MEMOIZED_RECURSIVE: return new MemoizedRecursiveStrategy();
            default: throw new IllegalArgumentException("Unknown solver type: " + type);
        }
    }
}
