/**
 * Application entry point demonstrating pluggable pathfinding algorithms
 * and observer patterns for graph and algorithm observability.
 *
 * Command-line options:
 * - (no args): Run with default Dijkstra
 * - --dijkstra: Explicitly use Dijkstra
 * - --bellman-ford: Use Bellman-Ford
 * - --metrics: Show metrics for chosen algorithm
 * - --all: Compare all available algorithms
 * - --observe: Enable graph and algorithm observers (logging, statistics)
 * - --help: Show usage information
 *
 * The main application logic doesn't change when switching algorithms—
 * only the algorithm instance changes via AlgorithmFactory.
 *
 * Observer Pattern enables:
 * - Real-time logging of graph modifications
 * - Algorithm progress tracking
 * - Statistics collection
 * - Side effects without core logic coupling
 */
class Main {
    public static void main(String[] args) {
        String algorithmName = "dijkstra";
        boolean showMetrics = false;
        boolean compareAll = false;
        boolean enableObservers = false;

        // Parse command-line arguments
        for (String arg : args) {
            if (arg.equals("--metrics")) {
                showMetrics = true;
            } else if (arg.equals("--all")) {
                compareAll = true;
            } else if (arg.equals("--observe")) {
                enableObservers = true;
            } else if (arg.equals("--help")) {
                printHelp();
                return;
            } else if (arg.startsWith("--")) {
                algorithmName = arg.substring(2);  // Remove -- prefix
            }
        }

        Graph graph = buildExampleGraph();

        if (enableObservers) {
            runWithObservers(graph, algorithmName);
        } else if (compareAll) {
            AlgorithmConfig config = AlgorithmConfig.create(0);
            ResultFormatter formatter = new ResultFormatter(config);
            runAllAlgorithms(graph, formatter);
        } else {
            AlgorithmConfig config = AlgorithmConfig.create(0);
            ResultFormatter formatter = new ResultFormatter(config);
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
     * Demonstrates observer pattern with logging and statistics.
     */
    private static void runWithObservers(Graph graph, String algorithmName) {
        System.out.println("=== Running with Observers ===");
        System.out.println();

        // Create observable graph and attach observers
        ObservableGraph observableGraph = new ObservableGraph(graph);
        LoggingGraphObserver graphLogger = new LoggingGraphObserver();
        StatisticsGraphObserver graphStats = new StatisticsGraphObserver();

        observableGraph.subscribe(graphLogger);
        observableGraph.subscribe(graphStats);

        System.out.println("Building graph with observers:");
        rebuildGraphWithObservers(observableGraph);

        System.out.println();
        System.out.println("Graph Statistics: " + graphStats);
        System.out.println();

        // Run algorithm with observers
        System.out.println("Running algorithm with observers:");
        PathfindingAlgorithm algorithm = AlgorithmFactory.create(algorithmName);
        ObservablePathfindingAlgorithm observableAlgorithm = new ObservablePathfindingAlgorithm(algorithm);

        LoggingAlgorithmObserver algoLogger = new LoggingAlgorithmObserver();
        StatisticsAlgorithmObserver algoStats = new StatisticsAlgorithmObserver();

        observableAlgorithm.subscribe(algoLogger);
        observableAlgorithm.subscribe(algoStats);

        ShortestPathResult result = observableAlgorithm.solve(observableGraph, 0);

        System.out.println();
        System.out.println("Algorithm Statistics: " + algoStats);

        System.out.println();
        System.out.println("Final Distances: ");
        ResultFormatter formatter = new ResultFormatter(AlgorithmConfig.create(0));
        formatter.printDistances(result);
    }

    /**
     * Rebuilds graph with observers attached.
     */
    private static void rebuildGraphWithObservers(ObservableGraph graph) {
        graph.addEdge(0, 1, 4);
        graph.addEdge(0, 2, 8);
        graph.addEdge(1, 4, 6);
        graph.addEdge(1, 2, 3);
        graph.addEdge(2, 3, 2);
        graph.addEdge(3, 4, 10);
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
        System.out.println("  --observe        Enable observers (logging, statistics)");
        System.out.println("  --help           Show this message");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  java Main                    # Default: Dijkstra");
        System.out.println("  java Main --bellman-ford     # Use Bellman-Ford");
        System.out.println("  java Main --dijkstra --metrics");
        System.out.println("  java Main --observe          # With observers");
        System.out.println("  java Main --all              # Compare all algorithms");
    }
}
