final class FloydWarshallDemo {

    private FloydWarshallDemo() {
        // Demo entrypoint only.
    }

    private static int[][] sampleGraph() {
        return new int[][] {
            {0, 4, FloydWarshall.INF, 5, FloydWarshall.INF},
            {FloydWarshall.INF, 0, 1, FloydWarshall.INF, 6},
            {2, FloydWarshall.INF, 0, 3, FloydWarshall.INF},
            {FloydWarshall.INF, FloydWarshall.INF, 1, 0, 2},
            {1, FloydWarshall.INF, FloydWarshall.INF, 4, 0}
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
        FloydWarshall.Result shortestPaths = FloydWarshall.computeShortestPaths(sampleGraph());
        printMatrix(shortestPaths.distances());
    }
}
