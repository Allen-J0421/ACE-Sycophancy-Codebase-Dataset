public final class FloydWarshall {
    public static final int NO_PATH = Integer.MAX_VALUE;
    public static final int INF = NO_PATH;

    private FloydWarshall() {
    }

    public static int[][] shortestPaths(int[][] graph) {
        return FloydWarshallSolver.solve(graph);
    }

    public static boolean hasNegativeCycle(int[][] distances) {
        MatrixValidator.requireSquare(distances, "Distance matrix");
        return MatrixUtils.hasNegativeDiagonalEntry(distances);
    }

    public static boolean isReachable(int distance) {
        return distance != NO_PATH;
    }
}
