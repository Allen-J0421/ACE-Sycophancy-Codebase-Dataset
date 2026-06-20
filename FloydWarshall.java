public final class FloydWarshall {
    public static final int NO_PATH = Integer.MAX_VALUE;
    public static final int INF = NO_PATH;
    private static final String OVERFLOW_MESSAGE = "Path distance overflowed int range.";

    private FloydWarshall() {
    }

    public static int[][] shortestPaths(int[][] graph) {
        int vertexCount = MatrixValidator.requireSquare(graph, "Graph matrix");
        int[][] distances = MatrixUtils.copyOf(graph);

        for (int via = 0; via < vertexCount; via++) {
            relaxPathsVia(distances, via);
        }

        return distances;
    }

    public static boolean hasNegativeCycle(int[][] distances) {
        MatrixValidator.requireSquare(distances, "Distance matrix");

        for (int index = 0; index < distances.length; index++) {
            if (hasNegativeSelfDistance(distances, index)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isReachable(int distance) {
        return distance != NO_PATH;
    }

    private static void relaxPathsVia(int[][] distances, int via) {
        int[] viaRow = distances[via];
        for (int[] fromRow : distances) {
            relaxRow(fromRow, viaRow, via);
        }
    }

    private static void relaxRow(int[] fromRow, int[] viaRow, int via) {
        int distanceToVia = fromRow[via];
        if (!isReachable(distanceToVia)) {
            return;
        }

        for (int to = 0; to < fromRow.length; to++) {
            relaxDistance(fromRow, viaRow, distanceToVia, to);
        }
    }

    private static void relaxDistance(int[] fromRow, int[] viaRow, int distanceToVia, int to) {
        int distanceFromVia = viaRow[to];
        if (!isReachable(distanceFromVia)) {
            return;
        }

        int candidateDistance = addDistances(distanceToVia, distanceFromVia);
        if (candidateDistance < fromRow[to]) {
            fromRow[to] = candidateDistance;
        }
    }

    private static boolean hasNegativeSelfDistance(int[][] distances, int vertex) {
        return distances[vertex][vertex] < 0;
    }

    private static int addDistances(int left, int right) {
        try {
            return Math.addExact(left, right);
        } catch (ArithmeticException exception) {
            throw new ArithmeticException(OVERFLOW_MESSAGE);
        }
    }
}
