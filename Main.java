import java.util.List;

public class Main {
    public static void main(String[] args) {
        Logger logger = new Logger.ConsoleLogger(false);

        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║     Advanced Graph Traversal & Analysis Framework Demo     ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");

        demonstrateGraphConstruction(logger);
        demonstrateGraphAnalysis(logger);
        demonstrateTraversalComparison(logger);
    }

    private static void demonstrateGraphConstruction(Logger logger) {
        System.out.println("─── GRAPH CONSTRUCTION WITH BUILDER ───\n");

        Result<Graph> graphResult = new GraphBuilder(6, logger)
                .addEdge(1, 2)
                .addEdge(0, 3)
                .addEdge(2, 0)
                .addEdge(5, 4)
                .buildResult();

        if (graphResult.isSuccess()) {
            System.out.println("✓ Graph successfully created\n");
            demonstrateGraphAnalysis(logger, graphResult.getOrNull());
        } else {
            System.out.println("✗ Failed to create graph: " + graphResult.getErrorMessage() + "\n");
        }
    }

    private static void demonstrateGraphAnalysis(Logger logger) {
        Graph graph = buildSimpleGraph(logger);
        demonstrateGraphAnalysis(logger, graph);
    }

    private static void demonstrateGraphAnalysis(Logger logger, Graph graph) {
        System.out.println("─── COMPREHENSIVE GRAPH ANALYSIS ───\n");

        GraphAnalyzer analyzer = new GraphAnalyzer(graph, logger);
        GraphAnalyzer.AnalysisResult result = analyzer.analyze();

        System.out.println(result.getComprehensiveReport());
    }

    private static void demonstrateTraversalComparison(Logger logger) {
        System.out.println("\n─── TRAVERSAL ALGORITHM COMPARISON ───\n");

        Graph simpleGraph = buildSimpleGraph(logger);
        Graph largerGraph = buildLargerGraph(logger);

        compareTraversals(logger, simpleGraph, "Simple Graph");
        System.out.println();
        compareTraversals(logger, largerGraph, "Larger Graph");
    }

    private static void compareTraversals(Logger logger, Graph graph, String graphName) {
        System.out.println("Graph: " + graphName + " (" + graph.getVertexCount() + " vertices, " +
                graph.getEdgeCount() + " edges)\n");

        GraphAnalyzer analyzer = new GraphAnalyzer(graph, logger);

        TraversalStats dfsRecursive = analyzer.traverseWithDFS(true);
        TraversalStats dfsIterative = analyzer.traverseWithDFS(false);
        TraversalStats bfs = analyzer.traverseWithBFS();

        printTraversalStats(dfsRecursive);
        printTraversalStats(dfsIterative);
        printTraversalStats(bfs);

        System.out.println();
    }

    private static void printTraversalStats(TraversalStats stats) {
        System.out.println(stats.getReport());
    }

    private static Graph buildSimpleGraph(Logger logger) {
        return new GraphBuilder(6, logger)
                .addEdge(1, 2)
                .addEdge(0, 3)
                .addEdge(2, 0)
                .addEdge(5, 4)
                .build();
    }

    private static Graph buildLargerGraph(Logger logger) {
        GraphBuilder builder = new GraphBuilder(10, logger);
        // Create a more connected graph
        builder.addEdge(0, 1).addEdge(0, 2).addEdge(1, 3).addEdge(2, 3)
               .addEdge(3, 4).addEdge(4, 5).addEdge(5, 6).addEdge(6, 7)
               .addEdge(7, 8).addEdge(8, 9).addEdge(9, 0);
        return builder.build();
    }
}
