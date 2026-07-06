import java.io.PrintStream;

class RodCuttingStrategyFactory {
    private final PrintStream log;

    RodCuttingStrategyFactory(PrintStream log) {
        this.log = log;
    }

    RodCuttingStrategy create(SolverType type) {
        return new LoggingDecorator(bareStrategy(type), log);
    }

    private RodCuttingStrategy bareStrategy(SolverType type) {
        switch (type) {
            case ITERATIVE_DP:       return new IterativeDPStrategy();
            case MEMOIZED_RECURSIVE: return new MemoizedRecursiveStrategy();
            default: throw new IllegalArgumentException("Unknown solver type: " + type);
        }
    }
}
