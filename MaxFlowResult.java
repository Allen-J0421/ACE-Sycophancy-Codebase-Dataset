public final class MaxFlowResult {
    private final int maxFlow;
    private final int[][] residualGraph;

    public MaxFlowResult(int maxFlow, int[][] residualGraph) {
        this.maxFlow = maxFlow;
        this.residualGraph = copyOf(residualGraph);
    }

    public int getMaxFlow() {
        return maxFlow;
    }

    public int[][] copyResidualGraph() {
        return copyOf(residualGraph);
    }

    private static int[][] copyOf(int[][] source) {
        int[][] copy = new int[source.length][source.length];
        for (int row = 0; row < source.length; row++) {
            System.arraycopy(source[row], 0, copy[row], 0, source.length);
        }
        return copy;
    }
}
