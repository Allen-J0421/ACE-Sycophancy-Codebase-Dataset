public final class MaxFlow {
    public int fordFulkerson(int[][] graph, int source, int sink) {
        return maximumFlow(graph, source, sink);
    }

    public int maximumFlow(int[][] graph, int source, int sink) {
        CapacityMatrixValidator.validate(graph, source, sink);

        ResidualNetwork residualNetwork = ResidualNetwork.from(graph);
        int[] parent = new int[graph.length];
        int maxFlow = 0;

        while (residualNetwork.findAugmentingPath(source, sink, parent)) {
            int pathFlow = residualNetwork.bottleneckCapacity(source, sink, parent);
            residualNetwork.augmentPath(source, sink, parent, pathFlow);
            maxFlow = Math.addExact(maxFlow, pathFlow);
        }

        return maxFlow;
    }

    public static void main(String[] args) {
        MaxFlowDemo.main(args);
    }
}
