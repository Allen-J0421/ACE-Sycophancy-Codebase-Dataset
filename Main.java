/**
 * Application entry point for Dijkstra's shortest path solver.
 * Demonstrates basic usage and metrics tracking capabilities.
 */
class Main {
    public static void main(String[] args) {
        boolean showMetrics = args.length > 0 && args[0].equals("--metrics");

        Graph graph = GraphBuilder.withVertexCount(5)
            .addEdge(0, 1, 4)
            .addEdge(0, 2, 8)
            .addEdge(1, 4, 6)
            .addEdge(1, 2, 3)
            .addEdge(2, 3, 2)
            .addEdge(3, 4, 10)
            .build();

        AlgorithmConfig config = AlgorithmConfig.create(0);
        ResultFormatter formatter = new ResultFormatter(config);

        if (showMetrics) {
            runWithMetrics(graph, formatter);
        } else {
            runBasic(graph, formatter);
        }
    }

    private static void runBasic(Graph graph, ResultFormatter formatter) {
        DijkstraShortestPathSolver solver = new DijkstraShortestPathSolver();
        ShortestPathResult result = solver.solve(graph, 0);
        formatter.printDistances(result);
    }

    private static void runWithMetrics(Graph graph, ResultFormatter formatter) {
        MetricsTrackingSolver solver = new MetricsTrackingSolver();
        ShortestPathResult result = solver.solveWithMetrics(graph, 0);

        formatter.printDistances(result);
        System.out.println();
        System.out.println(solver.getMetrics());
    }
}
