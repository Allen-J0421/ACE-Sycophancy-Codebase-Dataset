public final class MaxFlowSolver {
    private final AugmentingPathFinder pathFinder;

    public MaxFlowSolver() {
        this(new BreadthFirstAugmentingPathFinder());
    }

    public MaxFlowSolver(AugmentingPathFinder pathFinder) {
        if (pathFinder == null) {
            throw new IllegalArgumentException("Path finder must not be null.");
        }
        this.pathFinder = pathFinder;
    }

    public MaxFlowResult solve(FlowProblem problem) {
        ResidualNetwork residualNetwork = new ResidualNetwork(problem.network());
        int maxFlow = 0;

        while (true) {
            java.util.Optional<AugmentingPath> path = pathFinder.find(residualNetwork, problem);
            if (path.isEmpty()) {
                return new MaxFlowResult(maxFlow, residualNetwork.snapshot());
            }

            residualNetwork.augment(path.get());
            maxFlow += path.get().bottleneck();
        }
    }

    public MaxFlowResult solve(FlowNetwork network, int source, int sink) {
        return solve(new FlowProblem(network, source, sink));
    }

    public int calculateMaxFlow(int[][] graph, int source, int sink) {
        return solve(new FlowNetwork(graph), source, sink).getMaxFlow();
    }
}
