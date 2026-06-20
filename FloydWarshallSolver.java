final class FloydWarshallSolver {
    private static final String OVERFLOW_MESSAGE = "Path distance overflowed int range.";

    private final int[][] distances;
    private final int vertexCount;

    private FloydWarshallSolver(int[][] graph) {
        vertexCount = MatrixValidator.requireSquare(graph, "Graph matrix");
        distances = MatrixUtils.copyOf(graph);
    }

    static int[][] solve(int[][] graph) {
        return new FloydWarshallSolver(graph).solve();
    }

    private int[][] solve() {
        for (int via = 0; via < vertexCount; via++) {
            relaxPathsVia(via);
        }

        return distances;
    }

    private void relaxPathsVia(int via) {
        int[] viaRow = distances[via];
        for (int[] fromRow : distances) {
            relaxRow(fromRow, viaRow, via);
        }
    }

    private void relaxRow(int[] fromRow, int[] viaRow, int via) {
        int distanceToVia = fromRow[via];
        if (!FloydWarshall.isReachable(distanceToVia)) {
            return;
        }

        for (int to = 0; to < fromRow.length; to++) {
            relaxDistance(fromRow, viaRow, distanceToVia, to);
        }
    }

    private void relaxDistance(int[] fromRow, int[] viaRow, int distanceToVia, int to) {
        int distanceFromVia = viaRow[to];
        if (!FloydWarshall.isReachable(distanceFromVia)) {
            return;
        }

        int candidateDistance = addDistances(distanceToVia, distanceFromVia);
        if (candidateDistance < fromRow[to]) {
            fromRow[to] = candidateDistance;
        }
    }

    private int addDistances(int left, int right) {
        try {
            return Math.addExact(left, right);
        } catch (ArithmeticException exception) {
            throw new ArithmeticException(OVERFLOW_MESSAGE);
        }
    }
}
