package maxflow;

public final class MaxFlowResult {
    private final int value;
    private final int source;
    private final int sink;
    private final int[][] residualCapacities;

    MaxFlowResult(int value, int source, int sink, int[][] residualCapacities) {
        this.value = value;
        this.source = source;
        this.sink = sink;
        this.residualCapacities = copyMatrix(residualCapacities);
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
        return residualCapacities.length;
    }

    public int residualCapacity(int from, int to) {
        return residualCapacities[from][to];
    }

    public boolean hasResidualCapacity(int from, int to) {
        return residualCapacities[from][to] > 0;
    }

    private static int[][] copyMatrix(int[][] sourceMatrix) {
        int[][] copy = new int[sourceMatrix.length][sourceMatrix.length];
        for (int row = 0; row < sourceMatrix.length; row++) {
            System.arraycopy(sourceMatrix[row], 0, copy[row], 0, sourceMatrix[row].length);
        }
        return copy;
    }
}
