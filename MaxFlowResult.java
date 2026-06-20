public final class MaxFlowResult {
    private final FlowProblem problem;
    private final int maxFlow;
    private final FlowNetwork residualNetwork;

    public MaxFlowResult(FlowProblem problem, int maxFlow, FlowNetwork residualNetwork) {
        if (problem == null) {
            throw new IllegalArgumentException("Problem must not be null.");
        }
        if (residualNetwork == null) {
            throw new IllegalArgumentException("Residual network must not be null.");
        }

        this.problem = problem;
        this.maxFlow = maxFlow;
        this.residualNetwork = residualNetwork;
    }

    public FlowProblem problem() {
        return problem;
    }

    public int maxFlow() {
        return maxFlow;
    }

    public FlowNetwork residualNetwork() {
        return residualNetwork;
    }

    public int getMaxFlow() {
        return maxFlow;
    }

    public int[][] copyResidualGraph() {
        return residualNetwork.copyCapacities();
    }
}
