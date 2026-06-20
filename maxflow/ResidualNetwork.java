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
        for (int index = 0; index < path.vertexCount() - 1; index++) {
            int from = path.vertexAt(index);
            int to = path.vertexAt(index + 1);
            residualCapacities.add(from, to, -flow);
            residualCapacities.add(to, from, flow);
        }

        return flow;
    }

    int[][] snapshotCapacities() {
        return residualCapacities.snapshot();
    }
}
