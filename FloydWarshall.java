public final class FloydWarshall {
    public static final int INF = 100_000_000;

    private FloydWarshall() {
    }

    public static int[][] shortestPaths(int[][] graph) {
        validateSquareMatrix(graph);

        int vertexCount = graph.length;
        int[][] distances = copyMatrix(graph);

        for (int intermediate = 0; intermediate < vertexCount; intermediate++) {
            for (int source = 0; source < vertexCount; source++) {
                for (int target = 0; target < vertexCount; target++) {
                    if (distances[source][intermediate] == INF
                            || distances[intermediate][target] == INF) {
                        continue;
                    }

                    int candidateDistance = addDistances(
                            distances[source][intermediate],
                            distances[intermediate][target]);
                    if (candidateDistance < distances[source][target]) {
                        distances[source][target] = candidateDistance;
                    }
                }
            }
        }

        return distances;
    }

    public static boolean hasNegativeCycle(int[][] distances) {
        validateSquareMatrix(distances);

        for (int vertex = 0; vertex < distances.length; vertex++) {
            if (distances[vertex][vertex] < 0) {
                return true;
            }
        }

        return false;
    }

    private static int[][] copyMatrix(int[][] matrix) {
        int[][] copy = new int[matrix.length][matrix.length];
        for (int row = 0; row < matrix.length; row++) {
            System.arraycopy(matrix[row], 0, copy[row], 0, matrix.length);
        }
        return copy;
    }

    private static int addDistances(int firstDistance, int secondDistance) {
        try {
            return Math.addExact(firstDistance, secondDistance);
        } catch (ArithmeticException exception) {
            throw new ArithmeticException("Path distance overflowed int range.");
        }
    }

    private static void validateSquareMatrix(int[][] matrix) {
        if (matrix == null) {
            throw new IllegalArgumentException("Graph matrix cannot be null.");
        }

        for (int row = 0; row < matrix.length; row++) {
            if (matrix[row] == null || matrix[row].length != matrix.length) {
                throw new IllegalArgumentException("Graph matrix must be square.");
            }
        }
    }

    private static void printDistances(int[][] distances) {
        for (int[] row : distances) {
            for (int distance : row) {
                System.out.print((distance == INF ? "INF" : distance) + " ");
            }
            System.out.println();
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

        int[][] distances = shortestPaths(graph);
        printDistances(distances);
    }
}
