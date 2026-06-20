public final class MaxFlowSolver {
    public MaxFlowResult solve(FlowProblem problem) {
        ResidualNetwork residualNetwork = new ResidualNetwork(problem.network());
        int maxFlow = 0;

        while (true) {
            java.util.Optional<AugmentingPath> path = residualNetwork.findAugmentingPath(problem.source(), problem.sink());
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
