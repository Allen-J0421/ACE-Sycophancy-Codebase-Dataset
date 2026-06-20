public final class FloydWarshall {
    public static final int NO_PATH = Integer.MAX_VALUE;
    public static final int INF = NO_PATH;
    private static final String OVERFLOW_MESSAGE = "Path distance overflowed int range.";

    private FloydWarshall() {
    }

    public static int[][] shortestPaths(int[][] graph) {
        int vertexCount = Matrices.requireSquare(graph, "Graph matrix");
        int[][] distances = Matrices.copyOf(graph);

        for (int via = 0; via < vertexCount; via++) {
            relaxPathsVia(distances, vertexCount, via);
        }

        return distances;
    }

    public static boolean hasNegativeCycle(int[][] distances) {
        Matrices.requireSquare(distances, "Distance matrix");

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

    private static void relaxPathsVia(int[][] distances, int vertexCount, int via) {
        int[] viaRow = distances[via];
        for (int from = 0; from < vertexCount; from++) {
            int[] fromRow = distances[from];
            for (int to = 0; to < vertexCount; to++) {
                relax(fromRow, viaRow, via, to);
            }
        }
    }

    private static void relax(int[] fromRow, int[] viaRow, int via, int to) {
        if (!canRelax(fromRow, viaRow, via, to)) {
            return;
        }

        int candidateDistance = pathDistanceThrough(fromRow, viaRow, via, to);
        if (isShorterPath(candidateDistance, fromRow[to])) {
            fromRow[to] = candidateDistance;
        }
    }

    private static boolean hasNegativeSelfDistance(int[][] distances, int vertex) {
        return distances[vertex][vertex] < 0;
    }

    private static boolean canRelax(int[] fromRow, int[] viaRow, int via, int to) {
        return isReachable(distanceToVia(fromRow, via)) && isReachable(viaRow[to]);
    }

    private static boolean isShorterPath(int candidateDistance, int currentDistance) {
        return candidateDistance < currentDistance;
    }

    private static int pathDistanceThrough(int[] fromRow, int[] viaRow, int via, int to) {
        return addDistances(distanceToVia(fromRow, via), viaRow[to]);
    }

    private static int distanceToVia(int[] fromRow, int via) {
        return fromRow[via];
    }

    private static int addDistances(int left, int right) {
        try {
            return Math.addExact(left, right);
        } catch (ArithmeticException exception) {
            throw new ArithmeticException(OVERFLOW_MESSAGE);
        }
    }
}
