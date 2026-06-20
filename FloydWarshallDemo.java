final class FloydWarshallDemo {

    private FloydWarshallDemo() {
        // Demo entrypoint only.
    }

    private static int[][] sampleGraph() {
        return new int[][] {
            {0, 4, WeightedGraph.DEFAULT_UNREACHABLE_DISTANCE, 5, WeightedGraph.DEFAULT_UNREACHABLE_DISTANCE},
            {WeightedGraph.DEFAULT_UNREACHABLE_DISTANCE, 0, 1, WeightedGraph.DEFAULT_UNREACHABLE_DISTANCE, 6},
            {2, WeightedGraph.DEFAULT_UNREACHABLE_DISTANCE, 0, 3, WeightedGraph.DEFAULT_UNREACHABLE_DISTANCE},
            {WeightedGraph.DEFAULT_UNREACHABLE_DISTANCE, WeightedGraph.DEFAULT_UNREACHABLE_DISTANCE, 1, 0, 2},
            {1, WeightedGraph.DEFAULT_UNREACHABLE_DISTANCE, WeightedGraph.DEFAULT_UNREACHABLE_DISTANCE, 4, 0}
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
        WeightedGraph graph = WeightedGraph.from(sampleGraph());
        FloydWarshall solver = FloydWarshall.from(graph);
        FloydWarshall.Result shortestPaths = solver.solve();
        printMatrix(shortestPaths.distances());
    }
}
