public final class MaxFlow {
    public int fordFulkerson(int[][] graph, int source, int sink) {
        return maximumFlow(graph, source, sink);
    }

    public int maximumFlow(int[][] graph, int source, int sink) {
        validateGraph(graph, source, sink);

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

    private static void validateGraph(int[][] graph, int source, int sink) {
        if (graph == null || graph.length == 0) {
            throw new IllegalArgumentException("Graph must contain at least one vertex.");
        }

        if (!isValidVertex(source, graph.length) || !isValidVertex(sink, graph.length)) {
            throw new IllegalArgumentException("Source and sink must be valid vertex indexes.");
        }

        if (source == sink) {
            throw new IllegalArgumentException("Source and sink must be different vertices.");
        }

        for (int row = 0; row < graph.length; row++) {
            if (graph[row] == null || graph[row].length != graph.length) {
                throw new IllegalArgumentException("Graph must be a square capacity matrix.");
            }

            for (int capacity : graph[row]) {
                if (capacity < 0) {
                    throw new IllegalArgumentException("Graph capacities must be non-negative.");
                }
            }
        }
    }

    private static boolean isValidVertex(int vertex, int vertexCount) {
        return vertex >= 0 && vertex < vertexCount;
    }

    public static void main(String[] args) {
        MaxFlowDemo.main(args);
    }
}
