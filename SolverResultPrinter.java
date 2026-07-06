import java.io.PrintStream;

class SolverResultPrinter {
    private final PrintStream out;

    SolverResultPrinter(PrintStream out) {
        this.out = out;
    }

    void print(SolverType type, RodCuttingSolution solution) {
        out.println(type + " - max revenue: " + solution.maxRevenue() + ", cuts: " + solution.cuts());
    }
}
