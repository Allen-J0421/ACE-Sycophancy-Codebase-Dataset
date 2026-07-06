import java.io.PrintStream;

class ResultLogger {
    private final PrintStream out;

    ResultLogger(PrintStream out) {
        this.out = out;
    }

    void logTiming(String strategyName, long elapsedNs) {
        out.println(strategyName + " solved in " + elapsedNs + " ns");
    }

    void logSolution(SolverType type, RodCuttingSolution solution) {
        out.println(type + " - max revenue: " + solution.maxRevenue() + ", cuts: " + solution.cuts());
    }
}
