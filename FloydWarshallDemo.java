final class FloydWarshallDemo {

    private FloydWarshallDemo() {
        // Demo entrypoint only.
    }

    private static int[][] sampleGraph() {
        return new int[][] {
            {0, 4, FloydWarshall.DEFAULT_UNREACHABLE_DISTANCE, 5, FloydWarshall.DEFAULT_UNREACHABLE_DISTANCE},
            {FloydWarshall.DEFAULT_UNREACHABLE_DISTANCE, 0, 1, FloydWarshall.DEFAULT_UNREACHABLE_DISTANCE, 6},
            {2, FloydWarshall.DEFAULT_UNREACHABLE_DISTANCE, 0, 3, FloydWarshall.DEFAULT_UNREACHABLE_DISTANCE},
            {FloydWarshall.DEFAULT_UNREACHABLE_DISTANCE, FloydWarshall.DEFAULT_UNREACHABLE_DISTANCE, 1, 0, 2},
            {1, FloydWarshall.DEFAULT_UNREACHABLE_DISTANCE, FloydWarshall.DEFAULT_UNREACHABLE_DISTANCE, 4, 0}
        };
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
        FloydWarshall solver = FloydWarshall.from(sampleGraph());
        FloydWarshall.Result shortestPaths = solver.solve();
        printMatrix(shortestPaths.distances());
    }
}
