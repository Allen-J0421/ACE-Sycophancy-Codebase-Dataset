package maxflow;

public final class MaxFlowResult {
    private final int value;
    private final int source;
    private final int sink;
    private final SquareMatrix residualCapacities;

    MaxFlowResult(int value, int source, int sink, int[][] residualCapacities) {
        this(value, source, sink, SquareMatrix.copyOf(residualCapacities, "residual matrix"));
    }

    MaxFlowResult(int value, int source, int sink, SquareMatrix residualCapacities) {
        this.value = value;
        this.source = source;
        this.sink = sink;
        this.residualCapacities = residualCapacities.copy();
    }

    public int value() {
        return value;
    }

    public int source() {
        return source;
    }

    public int sink() {
        return sink;
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
}
