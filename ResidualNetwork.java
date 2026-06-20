public final class ResidualNetwork {
    private final int[][] capacities;

    public ResidualNetwork(FlowNetwork network) {
        capacities = network.copyCapacities();
    }

    public int size() {
        return capacities.length;
    }

    public boolean hasCapacity(Vertex from, Vertex to) {
        return capacity(from, to) > 0;
    }

    public int capacity(Vertex from, Vertex to) {
        return capacities[from.index()][to.index()];
    }

    public void augment(AugmentingPath path) {
        for (Vertex vertex = path.sink(); vertex.index() != path.source().index(); vertex = path.parentOf(vertex)) {
            Vertex previous = path.parentOf(vertex);
            capacities[previous.index()][vertex.index()] -= path.bottleneck();
            capacities[vertex.index()][previous.index()] += path.bottleneck();
        }
    }

    public int[][] snapshot() {
        return IntMatrix.copyOf(capacities);
    }
}
