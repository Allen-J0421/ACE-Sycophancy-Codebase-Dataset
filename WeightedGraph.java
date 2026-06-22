import java.util.Arrays;

final class WeightedGraph {
    private static final int NO_EDGE = 0;

    private final int[][] adjacencyMatrix;

    private WeightedGraph(int[][] adjacencyMatrix) {
        this.adjacencyMatrix = copyMatrix(adjacencyMatrix);
    }

    static WeightedGraph fromAdjacencyMatrix(int[][] adjacencyMatrix) {
        validateMatrix(adjacencyMatrix);
        return new WeightedGraph(adjacencyMatrix);
    }

    int vertexCount() {
        return adjacencyMatrix.length;
    }

    boolean hasEdge(int from, int to) {
        return adjacencyMatrix[from][to] != NO_EDGE;
    }

    int weightBetween(int from, int to) {
        return adjacencyMatrix[from][to];
    }

    private static void validateMatrix(int[][] adjacencyMatrix) {
        if (adjacencyMatrix == null || adjacencyMatrix.length == 0) {
            throw new IllegalArgumentException("Graph must contain at least one vertex.");
        }

        for (int row = 0; row < adjacencyMatrix.length; row++) {
            if (adjacencyMatrix[row] == null || adjacencyMatrix[row].length != adjacencyMatrix.length) {
                throw new IllegalArgumentException("Graph must be a square adjacency matrix.");
            }
        }

        for (int row = 0; row < adjacencyMatrix.length; row++) {
            if (adjacencyMatrix[row][row] != NO_EDGE) {
                throw new IllegalArgumentException("Graph diagonal must be zero.");
            }

            for (int column = row + 1; column < adjacencyMatrix.length; column++) {
                if (adjacencyMatrix[row][column] != adjacencyMatrix[column][row]) {
                    throw new IllegalArgumentException("Graph must be undirected and symmetric.");
                }
            }
        }
    }

    private static int[][] copyMatrix(int[][] adjacencyMatrix) {
        int[][] copiedMatrix = new int[adjacencyMatrix.length][];
        for (int row = 0; row < adjacencyMatrix.length; row++) {
            copiedMatrix[row] = Arrays.copyOf(adjacencyMatrix[row], adjacencyMatrix[row].length);
        }
        return copiedMatrix;
    }
}
