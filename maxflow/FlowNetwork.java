package maxflow;

public final class FlowNetwork {
    private final SquareMatrix capacities;

    private FlowNetwork(int[][] capacities) {
        this.capacities = SquareMatrix.copyOf(capacities, "capacity matrix");
    }

    public static FlowNetwork fromMatrix(int[][] capacities) {
        return new FlowNetwork(capacities);
    }

    public int vertexCount() {
        return capacities.size();
    }

    public ResidualNetwork createResidualNetwork() {
        return new ResidualNetwork(capacities);
    }
}
