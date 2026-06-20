public final class FlowProblem {
    private final FlowNetwork network;
    private final int source;
    private final int sink;

    public FlowProblem(FlowNetwork network, int source, int sink) {
        if (network == null) {
            throw new IllegalArgumentException("Network must not be null.");
        }
        if (source < 0 || source >= network.size() || sink < 0 || sink >= network.size()) {
            throw new IllegalArgumentException("Source and sink must be valid vertex indices.");
        }

        this.network = network;
        this.source = source;
        this.sink = sink;
    }

    public static FlowProblem fromCapacities(int[][] capacities, int source, int sink) {
        return new FlowProblem(new FlowNetwork(capacities), source, sink);
    }

    public FlowNetwork network() {
        return network;
    }

    public int source() {
        return source;
    }

    public int sink() {
        return sink;
    }
}
