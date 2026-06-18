/**
 * Application entry point demonstrating pluggable pathfinding algorithms.
 *
 * Command-line options:
 * - (no args): Run with default Dijkstra
 * - --dijkstra: Explicitly use Dijkstra
 * - --bellman-ford: Use Bellman-Ford
 * - --metrics: Show metrics for chosen algorithm
 * - --all: Compare all available algorithms
 * - --help: Show usage information
 *
 * The main application logic doesn't change when switching algorithms—
 * only the algorithm instance changes via AlgorithmFactory.
 */
class Main {
    public static void main(String[] args) {
        String algorithmName = "dijkstra";
        boolean showMetrics = false;
        boolean compareAll = false;

        // Parse command-line arguments
        for (String arg : args) {
            if (arg.equals("--metrics")) {
                showMetrics = true;
            } else if (arg.equals("--all")) {
                compareAll = true;
            } else if (arg.equals("--help")) {
                printHelp();
                return;
            } else if (arg.startsWith("--")) {
                algorithmName = arg.substring(2);  // Remove -- prefix
            }
        }

        Graph graph = buildExampleGraph();
        AlgorithmConfig config = AlgorithmConfig.create(0);
        ResultFormatter formatter = new ResultFormatter(config);

        if (compareAll) {
            runAllAlgorithms(graph, formatter);
        } else {
            runAlgorithm(algorithmName, graph, formatter, showMetrics);
        }
    }

    /**
     * Runs a single algorithm (no client code change needed to swap algorithms).
     */
    private static void runAlgorithm(String algorithmName, Graph graph,
                                     ResultFormatter formatter, boolean showMetrics) {
        try {
            PathfindingAlgorithm algorithm = AlgorithmFactory.create(algorithmName);
            System.out.println("Algorithm: " + algorithm.getName());
            System.out.println();

            ShortestPathResult result;
            if (showMetrics) {
                MetricsTrackingSolver metrics = new MetricsTrackingSolver(algorithm);
                result = metrics.solve(graph, 0);
                System.out.println("Distances: ");
                formatter.printDistances(result);
                System.out.println();
                System.out.println(metrics.getMetrics());
            } else {
                result = algorithm.solve(graph, 0);
                formatter.printDistances(result);
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
            System.err.println("Available: " + AlgorithmFactory.listAlgorithms());
        }
    }

    /**
     * Compares all available algorithms on the same graph.
     * Demonstrates how easily algorithms can be swapped.
     */
    private static void runAllAlgorithms(Graph graph, ResultFormatter formatter) {
        System.out.println("Comparing all algorithms:");
        System.out.println("=======================");
        System.out.println();

        String[] algorithms = {"dijkstra", "bellman-ford"};

        for (String algoName : algorithms) {
            try {
                PathfindingAlgorithm algorithm = AlgorithmFactory.create(algoName);
                System.out.println("Algorithm: " + algorithm.getName());

                MetricsTrackingSolver metrics = new MetricsTrackingSolver(algorithm);
                ShortestPathResult result = metrics.solve(graph, 0);

                System.out.print("Distances: ");
                formatter.printDistances(result);

                System.out.println("Time: " + metrics.getMetrics().getExecutionTimeMillis() +
                                 " ms");
                System.out.println();
            } catch (IllegalArgumentException e) {
                System.err.println("Skipping " + algoName + ": " + e.getMessage());
            }
        }
    }

    /**
     * Builds example graph for demonstration.
     */
    private static Graph buildExampleGraph() {
        return GraphBuilder.withVertexCount(5)
            .addEdge(0, 1, 4)
            .addEdge(0, 2, 8)
            .addEdge(1, 4, 6)
            .addEdge(1, 2, 3)
            .addEdge(2, 3, 2)
            .addEdge(3, 4, 10)
            .build();
    }

    /**
     * Prints help message with usage information.
     */
    private static void printHelp() {
        System.out.println("Shortest Path Solver - Usage");
        System.out.println("============================");
        System.out.println();
        System.out.println("java Main [OPTIONS]");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  --dijkstra       Use Dijkstra's algorithm (default)");
        System.out.println("  --bellman-ford   Use Bellman-Ford algorithm");
        System.out.println("  --metrics        Show execution metrics");
        System.out.println("  --all            Compare all available algorithms");
        System.out.println("  --help           Show this message");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  java Main                    # Default: Dijkstra");
        System.out.println("  java Main --bellman-ford     # Use Bellman-Ford");
        System.out.println("  java Main --dijkstra --metrics");
        System.out.println("  java Main --all              # Compare all algorithms");
    }
}
