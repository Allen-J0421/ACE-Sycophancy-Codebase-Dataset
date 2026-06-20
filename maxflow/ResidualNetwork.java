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

    public int bottleneckCapacity(AugmentingPath path) {
        int pathFlow = Integer.MAX_VALUE;

        for (int vertex = path.sink(); vertex != path.source(); vertex = path.parentOf(vertex)) {
            int predecessor = path.parentOf(vertex);
            pathFlow = Math.min(pathFlow, residualCapacities.get(predecessor, vertex));
        }

        return pathFlow;
    }

    public int augmentPath(AugmentingPath path) {
        int flow = bottleneckCapacity(path);
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
