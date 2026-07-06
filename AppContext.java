import java.io.PrintStream;

class AppContext {
    private final RodCuttingStrategyFactory strategyFactory;
    private final SolverResultPrinter printer;

    private AppContext(PrintStream out) {
        this.strategyFactory = new RodCuttingStrategyFactory(out);
        this.printer = new SolverResultPrinter(out);
    }

    static AppContext withOutput(PrintStream out) {
        return new AppContext(out);
    }

    RodCuttingSolver solverFor(SolverType type) {
        return new RodCuttingSolver(strategyFactory.create(type));
    }

    SolverResultPrinter printer() {
        return printer;
    }
}
