public final class ResidualNetwork {
    private final int[][] capacities;

    public ResidualNetwork(FlowNetwork network) {
        capacities = network.copyCapacities();
    }

    public int size() {
        return capacities.length;
    }

    public boolean hasCapacity(int from, int to) {
        return capacities[from][to] > 0;
    }

    public int capacity(int from, int to) {
        return capacities[from][to];
    }

    public void augment(AugmentingPath path) {
        for (int vertex = path.sink(); vertex != path.source(); vertex = path.parentOf(vertex)) {
            int previous = path.parentOf(vertex);
            capacities[previous][vertex] -= path.bottleneck();
            capacities[vertex][previous] += path.bottleneck();
        }
    }

    public int[][] snapshot() {
        return IntMatrix.copyOf(capacities);
    }
}
