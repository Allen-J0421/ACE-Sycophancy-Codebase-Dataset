package maxflow;

public final class ResidualNetwork {
    private final SquareMatrix residualCapacities;

    ResidualNetwork(SquareMatrix capacities) {
        this.residualCapacities = capacities.copy();
    }

    public int vertexCount() {
        return residualCapacities.size();
    }

    public int residualCapacity(int from, int to) {
        return residualCapacities.get(from, to);
    }

    public boolean hasResidualCapacity(int from, int to) {
        return residualCapacities.hasPositiveValue(from, to);
    }

    public int augmentPath(AugmentingPath path) {
        int flow = path.flow();
        for (int vertex = path.sink(); vertex != path.source(); vertex = path.parentOf(vertex)) {
            int predecessor = path.parentOf(vertex);
            residualCapacities.add(predecessor, vertex, -flow);
            residualCapacities.add(vertex, predecessor, flow);
        }

        return flow;
    }

    int[][] snapshotCapacities() {
        return residualCapacities.snapshot();
    }
}
