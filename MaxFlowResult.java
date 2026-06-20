public final class MaxFlowResult {
    private final int maxFlow;
    private final int[][] residualGraph;

    public MaxFlowResult(int maxFlow, int[][] residualGraph) {
        this.maxFlow = maxFlow;
        this.residualGraph = IntMatrix.copyOf(residualGraph);
    }

    public int getMaxFlow() {
        return maxFlow;
    }

    public int[][] copyResidualGraph() {
        return IntMatrix.copyOf(residualGraph);
    }
}
