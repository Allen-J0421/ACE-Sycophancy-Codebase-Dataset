class RodCuttingSolver {
    private final RodCuttingStrategy strategy;

    RodCuttingSolver(RodCuttingStrategy strategy) {
        this.strategy = strategy;
    }

    RodCuttingSolution solve(RodCuttingProblem problem) {
        return strategy.solve(problem);
    }
}
