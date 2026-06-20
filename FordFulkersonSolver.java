public final class FordFulkersonSolver implements MaxFlowAlgorithm {
    private final AugmentingPathFinder pathFinder;

    public FordFulkersonSolver() {
        this(new BreadthFirstAugmentingPathFinder());
    }

    public FordFulkersonSolver(AugmentingPathFinder pathFinder) {
        if (pathFinder == null) {
            throw new IllegalArgumentException("Path finder must not be null.");
        }
        this.pathFinder = pathFinder;
    }

    @Override
    public MaxFlowResult solve(FlowProblem problem) {
        ResidualNetwork residualNetwork = new ResidualNetwork(problem.network());
        int maxFlow = 0;

        while (true) {
            java.util.Optional<AugmentingPath> path = pathFinder.find(residualNetwork, problem);
            if (path.isEmpty()) {
                return new MaxFlowResult(problem, maxFlow, residualNetwork.asFlowNetwork());
            }

            residualNetwork.augment(path.get());
            maxFlow += path.get().bottleneck();
        }
    }
}
