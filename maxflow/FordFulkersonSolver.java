package maxflow;

import java.util.Optional;

public final class FordFulkersonSolver implements MaxFlowSolver {
    private final FlowNetwork network;
    private final int source;
    private final int sink;
    private final AugmentingPathFinder augmentingPathFinder;

    public FordFulkersonSolver(FlowNetwork network, int source, int sink) {
        this(network, source, sink, new BreadthFirstAugmentingPathFinder());
    }

    public FordFulkersonSolver(
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
        this.source = validateVertex(source, network.vertexCount(), "source");
        this.sink = validateVertex(sink, network.vertexCount(), "sink");
        this.augmentingPathFinder = augmentingPathFinder;

        if (source == sink) {
            throw new IllegalArgumentException("source and sink must be different vertices");
        }
    }

    @Override
    public int computeMaxFlow() {
        ResidualNetwork residualNetwork = network.createResidualNetwork();
        int maxFlow = 0;

        Optional<AugmentingPath> augmentingPath =
            augmentingPathFinder.findPath(residualNetwork, source, sink);
        while (augmentingPath.isPresent()) {
            AugmentingPath path = augmentingPath.get();
            maxFlow += residualNetwork.augmentPath(path);
            augmentingPath = augmentingPathFinder.findPath(residualNetwork, source, sink);
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
