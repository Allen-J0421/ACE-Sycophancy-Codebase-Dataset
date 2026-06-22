import java.util.ArrayList;
import java.util.List;

class Graph {
    private final int[][] adjacencyMatrix;
    private final int vertexCount;

    static Graph fromMatrix(int[][] matrix) {
        return new Graph(matrix);
    }

    private Graph(int[][] matrix) {
        validate(matrix);
        this.vertexCount = matrix.length;
        this.adjacencyMatrix = copyMatrix(matrix);
    }

    int vertexCount() {
        return vertexCount;
    }

    List<Edge> edgesFrom(int vertex) {
        List<Edge> edges = new ArrayList<>();
        for (int to = 0; to < vertexCount; to++) {
            int w = adjacencyMatrix[vertex][to];
            if (w != 0) {
                edges.add(new Edge(vertex, to, w));
            }
        }
        return edges;
    }

    private static void validate(int[][] matrix) {
        if (matrix == null || matrix.length == 0) {
            throw new IllegalArgumentException("Graph matrix must be non-empty");
        }
        int n = matrix.length;
        for (int[] row : matrix) {
            if (row == null || row.length != n) {
                throw new IllegalArgumentException("Graph matrix must be square");
            }
        }
        for (int i = 0; i < n; i++) {
            if (matrix[i][i] != 0) {
                throw new IllegalArgumentException("Graph matrix diagonal must be zero (no self-loops)");
            }
            for (int j = i + 1; j < n; j++) {
                if (matrix[i][j] != matrix[j][i]) {
                    throw new IllegalArgumentException(
                        "Graph matrix must be symmetric (undirected graph)");
                }
            }
        }
    }

    private static int[][] copyMatrix(int[][] matrix) {
        int n = matrix.length;
        int[][] copy = new int[n][n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(matrix[i], 0, copy[i], 0, n);
        }
        return copy;
    }
}
