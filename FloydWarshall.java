public final class FloydWarshall {
    public static final int INF = Integer.MAX_VALUE;

    private FloydWarshall() {
    }

    public static int[][] shortestPaths(int[][] graph) {
        int vertexCount = MatrixValidator.requireSquare(graph, "Graph matrix");
        int[][] distances = MatrixUtils.copyOf(graph);

        for (int intermediate = 0; intermediate < vertexCount; intermediate++) {
            for (int source = 0; source < vertexCount; source++) {
                for (int target = 0; target < vertexCount; target++) {
                    relax(distances, source, intermediate, target);
                }
            }
        }

        return distances;
    }

    public static boolean hasNegativeCycle(int[][] distances) {
        MatrixValidator.requireSquare(distances, "Distance matrix");

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

    private static void relax(
            int[][] distances,
            int source,
            int intermediate,
            int target) {
        if (!canRelax(distances, source, intermediate, target)) {
            return;
        }

        int candidateDistance = pathDistanceThrough(
                distances, source, intermediate, target);
        if (candidateDistance < distances[source][target]) {
            distances[source][target] = candidateDistance;
        }
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

    private static int addDistances(int firstDistance, int secondDistance) {
        try {
            return Math.addExact(firstDistance, secondDistance);
        } catch (ArithmeticException exception) {
            throw new ArithmeticException("Path distance overflowed int range.");
        }
    }

}
