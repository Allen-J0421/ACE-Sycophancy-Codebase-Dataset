import java.io.PrintStream;

class LoggingDecorator implements RodCuttingStrategy {
    private final RodCuttingStrategy delegate;
    private final PrintStream log;

    LoggingDecorator(RodCuttingStrategy delegate, PrintStream log) {
        this.delegate = delegate;
        this.log = log;
    }

    @Override
    public RodCuttingSolution solve(RodCuttingProblem problem) {
        long start = System.nanoTime();
        RodCuttingSolution solution = delegate.solve(problem);
        long elapsedNs = System.nanoTime() - start;
        log.println(delegate.getClass().getSimpleName() + " solved in " + elapsedNs + " ns");
        return solution;
    }
}
