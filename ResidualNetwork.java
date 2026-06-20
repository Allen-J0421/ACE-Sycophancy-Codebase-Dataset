public final class ResidualNetwork {
    private final int[][] capacities;
    private final Vertex[] vertices;

    public ResidualNetwork(FlowNetwork network) {
        capacities = network.copyCapacities();
        vertices = network.vertices();
    }

    public int size() {
        return capacities.length;
    }

    public boolean hasCapacity(Vertex from, Vertex to) {
        return capacity(from, to) > 0;
    }

    public Vertex vertexAt(int index) {
        return vertices[index];
    }

    public Vertex[] vertices() {
        return vertices.clone();
    }

    public int capacity(Vertex from, Vertex to) {
        return capacities[from.index()][to.index()];
    }

    public void augment(AugmentingPath path) {
        for (Vertex vertex = path.sink(); !vertex.equals(path.source()); vertex = path.parentOf(vertex)) {
            Vertex previous = path.parentOf(vertex);
            capacities[previous.index()][vertex.index()] -= path.bottleneck();
            capacities[vertex.index()][previous.index()] += path.bottleneck();
        }
    }

    public int[][] snapshot() {
        return IntMatrix.copyOf(capacities);
    }
}
