class Graph {
    private final int[][] adjacencyMatrix;
    private final int vertexCount;

    Graph(int[][] matrix) {
        validate(matrix);
        this.vertexCount = matrix.length;
        this.adjacencyMatrix = copyMatrix(matrix);
    }

    int vertexCount() {
        return vertexCount;
    }

    int weight(int from, int to) {
        return adjacencyMatrix[from][to];
    }

    private static void validate(int[][] matrix) {
        if (matrix == null || matrix.length == 0) {
            throw new IllegalArgumentException("Graph matrix must be non-empty");
        }
        for (int[] row : matrix) {
            if (row == null || row.length != matrix.length) {
                throw new IllegalArgumentException("Graph matrix must be square");
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
