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
        FlowNetwork network = FlowNetwork.fromCapacities(capacities);
        return new FlowProblem(network, network.vertexAt(source), network.vertexAt(sink));
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

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof FlowProblem)) {
            return false;
        }

        FlowProblem problem = (FlowProblem) other;
        return network.equals(problem.network)
            && source.equals(problem.source)
            && sink.equals(problem.sink);
    }

    @Override
    public int hashCode() {
        int result = network.hashCode();
        result = 31 * result + source.hashCode();
        result = 31 * result + sink.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "FlowProblem[source=" + source + ", sink=" + sink + ", network=" + network + "]";
    }
}
