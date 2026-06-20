package maxflow;

public final class FlowProblem {
    private final FlowGraph network;
    private final int source;
    private final int sink;

    private FlowProblem(FlowGraph network, int source, int sink) {
        if (network == null) {
            throw new IllegalArgumentException("network must not be null");
        }

        this.network = network;
        this.source = validateVertex(source, network.vertexCount(), "source");
        this.sink = validateVertex(sink, network.vertexCount(), "sink");

        if (source == sink) {
            throw new IllegalArgumentException("source and sink must be different vertices");
        }
    }

    public static FlowProblem of(FlowGraph network, int source, int sink) {
        return new FlowProblem(network, source, sink);
    }

    public FlowGraph network() {
        return network;
    }

    public int source() {
        return source;
    }

    public int sink() {
        return sink;
    }

    private static int validateVertex(int vertex, int vertexCount, String label) {
        if (vertex < 0 || vertex >= vertexCount) {
            throw new IllegalArgumentException(
                label + " vertex must be between 0 and " + (vertexCount - 1));
        }

        return vertex;
    }
}
