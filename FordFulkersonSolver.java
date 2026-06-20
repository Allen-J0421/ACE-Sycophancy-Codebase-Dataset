final class FordFulkersonSolver {
    private final FlowNetwork network;
    private final int source;
    private final int sink;
    private final AugmentingPathFinder augmentingPathFinder;

    FordFulkersonSolver(FlowNetwork network, int source, int sink) {
        this(network, source, sink, new BreadthFirstAugmentingPathFinder());
    }

    FordFulkersonSolver(
        FlowNetwork network,
        int source,
        int sink,
        AugmentingPathFinder augmentingPathFinder
    ) {
        if (network == null) {
            throw new IllegalArgumentException("network must not be null");
        }
        if (augmentingPathFinder == null) {
            throw new IllegalArgumentException("augmentingPathFinder must not be null");
        }

        this.network = network;
        this.source = validateVertex(source, network.size(), "source");
        this.sink = validateVertex(sink, network.size(), "sink");
        this.augmentingPathFinder = augmentingPathFinder;

        if (source == sink) {
            throw new IllegalArgumentException("source and sink must be different vertices");
        }
    }

    int computeMaxFlow() {
        ResidualNetwork residualNetwork = network.createResidualNetwork();
        int[] parent = new int[network.size()];
        int maxFlow = 0;

        while (augmentingPathFinder.findPath(residualNetwork, source, sink, parent)) {
            int pathFlow = residualNetwork.bottleneckCapacity(parent, source, sink);
            residualNetwork.augmentPath(parent, source, sink, pathFlow);
            maxFlow += pathFlow;
        }

        return maxFlow;
    }

    private static int validateVertex(int vertex, int vertexCount, String label) {
        if (vertex < 0 || vertex >= vertexCount) {
            throw new IllegalArgumentException(
                label + " vertex must be between 0 and " + (vertexCount - 1));
        }

        return vertex;
    }
}
