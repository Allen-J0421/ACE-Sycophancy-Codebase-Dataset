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

    public int[][] residualCapacities() {
        return residualNetwork.toCapacityMatrix();
    }

    public int getMaxFlow() {
        return maxFlow;
    }

    public int[][] copyResidualGraph() {
        return residualCapacities();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof MaxFlowResult)) {
            return false;
        }

        MaxFlowResult result = (MaxFlowResult) other;
        return maxFlow == result.maxFlow
            && problem.equals(result.problem)
            && residualNetwork.equals(result.residualNetwork);
    }

    @Override
    public int hashCode() {
        int result = problem.hashCode();
        result = 31 * result + maxFlow;
        result = 31 * result + residualNetwork.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "MaxFlowResult[maxFlow=" + maxFlow
            + ", problem=" + problem
            + ", residualNetwork=" + residualNetwork + "]";
    }
}
