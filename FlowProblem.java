public final class FlowProblem {
    private final FlowNetwork network;
    private final Vertex source;
    private final Vertex sink;

    public FlowProblem(FlowNetwork network, Vertex source, Vertex sink) {
        if (network == null) {
            throw new IllegalArgumentException("Network must not be null.");
        }
        if (!network.contains(source) || !network.contains(sink)) {
            throw new IllegalArgumentException("Source and sink must be valid vertices in the network.");
        }

        this.network = network;
        this.source = source;
        this.sink = sink;
    }

    public static FlowProblem fromCapacities(int[][] capacities, int source, int sink) {
        return new FlowProblem(new FlowNetwork(capacities), new Vertex(source), new Vertex(sink));
    }

    public FlowNetwork network() {
        return network;
    }

    public Vertex source() {
        return source;
    }

    public Vertex sink() {
        return sink;
    }
}
