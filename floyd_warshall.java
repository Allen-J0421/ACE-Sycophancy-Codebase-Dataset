final class FloydWarshall {

    private static final int INF = 100_000_000;

    private FloydWarshall() {
        // Utility class.
    }

    static int[][] computeShortestPaths(int[][] graph) {
        validateSquareMatrix(graph);

        int[][] dist = copyMatrix(graph);
        int vertices = dist.length;

        for (int k = 0; k < vertices; k++) {
            for (int i = 0; i < vertices; i++) {
                if (dist[i][k] == INF) {
                    continue;
                }

                for (int j = 0; j < vertices; j++) {
                    if (dist[k][j] == INF) {
                        continue;
                    }

                    int throughK = dist[i][k] + dist[k][j];
                    if (throughK < dist[i][j]) {
                        dist[i][j] = throughK;
                    }
                }
            }
        }

        return dist;
    }

    private static void validateSquareMatrix(int[][] matrix) {
        if (matrix == null) {
            throw new IllegalArgumentException("Matrix must not be null.");
        }

        for (int i = 0; i < matrix.length; i++) {
            if (matrix[i] == null) {
                throw new IllegalArgumentException("Matrix row " + i + " must not be null.");
            }

            if (matrix[i].length != matrix.length) {
                throw new IllegalArgumentException("Matrix must be square.");
            }
        }
    }

    private static int[][] copyMatrix(int[][] source) {
        int[][] copy = new int[source.length][];
        for (int i = 0; i < source.length; i++) {
            copy[i] = source[i].clone();
        }
        return copy;
    }

    private static void printMatrix(int[][] matrix) {
        for (int[] row : matrix) {
            StringBuilder line = new StringBuilder();
            for (int value : row) {
                if (line.length() > 0) {
                    line.append(' ');
                }
                line.append(value);
            }
            System.out.println(line);
        }
    }

    public static void main(String[] args) {
        int[][] graph = {
            {0, 4, INF, 5, INF},
            {INF, 0, 1, INF, 6},
            {2, INF, 0, 3, INF},
            {INF, INF, 1, 0, 2},
            {1, INF, INF, 4, 0}
        };

        int[][] shortestPaths = computeShortestPaths(graph);
        printMatrix(shortestPaths);
    }
}
