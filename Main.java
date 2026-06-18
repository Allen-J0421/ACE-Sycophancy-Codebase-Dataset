import graph.algorithm.BreadthFirstSearch;
import graph.algorithm.DepthFirstSearch;
import graph.algorithm.TraversalStats;
import graph.analysis.GraphAnalyzer;
import graph.config.GraphConfig;
import graph.core.Graph;
import graph.core.GraphBuilder;
import graph.utility.Logger;

public class Main {
    public static void main(String[] args) {
        Logger logger = new Logger.ConsoleLogger(false);

        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║      Advanced Graph Framework - Packaged Architecture      ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");

        demonstratePackagedArchitecture(logger);
        demonstrateAdvancedFeatures(logger);
    }

    private static void demonstratePackagedArchitecture(Logger logger) {
        System.out.println("─── PACKAGED ARCHITECTURE DEMO ───\n");

        GraphConfig config = GraphConfig.builder()
                .enableLogging(false)
                .cacheAnalysis(true)
                .cacheTtlMs(60000)
                .enableStats(true)
                .build();

        Graph graph = new GraphBuilder(6, config, logger)
                .addEdge(0, 1)
                .addEdge(1, 2)
                .addEdge(2, 0)
                .addEdge(3, 4)
                .addEdge(4, 5)
                .build();

        System.out.println("Graph created with configuration:");
        System.out.println("  Allow Self-Loops: " + config.isAllowSelfLoops());
        System.out.println("  Cache Analysis: " + config.isCacheAnalysis());
        System.out.println("  Cache TTL: " + config.getCacheTtlMs() + "ms");
        System.out.println("  Enable Logging: " + config.isEnableLogging());
        System.out.println("  Enable Stats: " + config.isEnableStats());
        System.out.println();
    }

    private static void demonstrateAdvancedFeatures(Logger logger) {
        System.out.println("─── ADVANCED FEATURES DEMO ───\n");

        Graph graph = new GraphBuilder(8)
                .addEdge(0, 1)
                .addEdge(1, 2)
                .addEdge(2, 3)
                .addEdge(3, 0)
                .addEdge(4, 5)
                .addEdge(5, 6)
                .addEdge(6, 7)
                .build();

        GraphAnalyzer analyzer = new GraphAnalyzer(graph, logger);
        GraphAnalyzer.AnalysisResult result = analyzer.analyze();

        System.out.println(result.getReport());

        System.out.println("\n─── TRAVERSAL COMPARISON ───\n");

        TraversalStats dfsR = analyzer.traverseWithDFS(true);
        TraversalStats dfsI = analyzer.traverseWithDFS(false);
        TraversalStats bfs = analyzer.traverseWithBFS();

        System.out.println(dfsR.getReport());
        System.out.println(dfsI.getReport());
        System.out.println(bfs.getReport());

        System.out.println("─── GRAPH OPERATIONS ───\n");

        System.out.println("Vertex Count: " + graph.getVertexCount());
        System.out.println("Edge Count: " + graph.getEdgeCount());
        System.out.println("Vertex 0 Degree: " + graph.getDegree(0));
        System.out.println("Neighbors of 0: " + graph.getNeighbors(0));
        System.out.println();
    }
}
