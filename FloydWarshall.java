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
                    if (!canRelax(distances, source, intermediate, target)) {
                        continue;
                    }

                    int candidateDistance = pathDistanceThrough(
                            distances, source, intermediate, target);
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

    public static boolean isReachable(int distance) {
        return distance != INF;
    }

    private static boolean canRelax(
            int[][] distances,
            int source,
            int intermediate,
            int target) {
        return isReachable(distances[source][intermediate])
                && isReachable(distances[intermediate][target]);
    }

    private static int pathDistanceThrough(
            int[][] distances,
            int source,
            int intermediate,
            int target) {
        return addDistances(
                distances[source][intermediate],
                distances[intermediate][target]);
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

}
