class LoggingDecorator implements RodCuttingStrategy {
    private final RodCuttingStrategy delegate;
    private final ResultLogger logger;

    LoggingDecorator(RodCuttingStrategy delegate, ResultLogger logger) {
        this.delegate = delegate;
        this.logger = logger;
    }

    @Override
    public RodCuttingSolution solve(RodCuttingProblem problem) {
        long start = System.nanoTime();
        RodCuttingSolution solution = delegate.solve(problem);
        logger.logTiming(delegate.getClass().getSimpleName(), System.nanoTime() - start);
        return solution;
    }
}
