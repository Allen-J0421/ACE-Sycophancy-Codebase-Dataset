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

    public static void main(String[] args) {
        WeightedGraph graph = WeightedGraph.from(sampleGraph());
        FloydWarshall solver = FloydWarshall.from(graph);
        AllPairsShortestPaths shortestPaths = solver.solve();
        System.out.println(shortestPaths.distances());
    }
}
