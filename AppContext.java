import java.io.PrintStream;

class AppContext {
    private final RodCuttingStrategyFactory strategyFactory;
    private final ResultLogger logger;

    private AppContext(PrintStream out) {
        this.logger = new ResultLogger(out);
        this.strategyFactory = new RodCuttingStrategyFactory(logger);
    }

    static AppContext withOutput(PrintStream out) {
        return new AppContext(out);
    }

    RodCuttingSolver solverFor(SolverType type) {
        return new RodCuttingSolver(strategyFactory.create(type));
    }

    ResultLogger logger() {
        return logger;
    }
}
