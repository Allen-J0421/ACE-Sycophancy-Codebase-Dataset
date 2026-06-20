package maxflow;

public final class FlowNetwork {
    private final int[][] capacities;

    private FlowNetwork(int[][] capacities) {
        validateCapacityMatrix(capacities);
        this.capacities = copyMatrix(capacities);
    }

    public static FlowNetwork fromMatrix(int[][] capacities) {
        return new FlowNetwork(capacities);
    }

    public int vertexCount() {
        return capacities.length;
    }

    public ResidualNetwork createResidualNetwork() {
        return new ResidualNetwork(capacities);
    }

    private static void validateCapacityMatrix(int[][] matrix) {
        if (matrix == null || matrix.length == 0) {
            throw new IllegalArgumentException("capacity matrix must not be null or empty");
        }

        if (matrix[0] == null) {
            throw new IllegalArgumentException("capacity matrix rows must not be null");
        }

        int width = matrix[0].length;
        if (width == 0) {
            throw new IllegalArgumentException("capacity matrix must contain at least one vertex");
        }

        if (width != matrix.length) {
            throw new IllegalArgumentException("capacity matrix must be square");
        }

        for (int row = 0; row < matrix.length; row++) {
            if (matrix[row] == null || matrix[row].length != width) {
                throw new IllegalArgumentException("capacity matrix must be rectangular");
            }
        }
    }

    private static int[][] copyMatrix(int[][] sourceMatrix) {
        int[][] copy = new int[sourceMatrix.length][sourceMatrix.length];
        for (int row = 0; row < sourceMatrix.length; row++) {
            System.arraycopy(sourceMatrix[row], 0, copy[row], 0, sourceMatrix[row].length);
        }
        return copy;
    }
}
