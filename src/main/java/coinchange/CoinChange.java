package coinchange;

import java.util.Objects;

public final class CoinChange implements CoinChangeSolver {
    private final CoinChangeSolver solver;

    public CoinChange(CoinChangeSolver solver) {
        this.solver = Objects.requireNonNull(solver, "solver must not be null");
    }

    @Override
    public CoinChangeSolution solve(CoinChangeProblem problem) {
        return solver.solve(problem);
    }

    public CoinChangeSolver solver() {
        return solver;
    }
}
