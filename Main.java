import java.util.List;
import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        Graph graph = buildSimpleGraph();
        GraphAnalyzer analyzer = new GraphAnalyzer(graph);
        GraphAnalyzer.AnalysisResult result = analyzer.analyze();

        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║           Graph Traversal and Analysis Demo              ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝\n");

        demonstrateAnalysis(result);
        demonstrateTraversals(result);
        demonstratePathFinding(result);

        System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║          Testing with Disconnected Graph                 ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝\n");

        Graph disconnectedGraph = buildDisconnectedGraph();
        GraphAnalyzer.AnalysisResult disconnectedResult = new GraphAnalyzer(disconnectedGraph).analyze();
        demonstrateAnalysis(disconnectedResult);
    }

    private static void demonstrateAnalysis(GraphAnalyzer.AnalysisResult result) {
        System.out.println("─── GRAPH ANALYSIS ───\n");

        System.out.println("Vertices:              " + result.getVertexCount());
        System.out.println("Edges:                 " + result.getEdgeCount());
        System.out.println("Connected Components:  " + result.getComponentCount());
        System.out.println("Is Connected:          " + result.isConnected());
        System.out.println("Has Cycle:             " + result.hasCycle());
        System.out.printf("Density:               %.3f%n", result.getDensity());

        System.out.println("\nConnected Components:");
        List<List<Integer>> components = result.getConnectedComponents();
        for (int i = 0; i < components.size(); i++) {
            System.out.println("  Component " + i + ": " + components.get(i));
        }
    }

    private static void demonstrateTraversals(GraphAnalyzer.AnalysisResult result) {
        System.out.println("\n─── TRAVERSAL ALGORITHMS ───\n");

        List<Integer> dfs = result.traverseDFS();
        System.out.println("DFS: " + formatPath(dfs));

        List<Integer> bfs = result.traverseBFS();
        System.out.println("BFS: " + formatPath(bfs));
    }

    private static void demonstratePathFinding(GraphAnalyzer.AnalysisResult result) {
        System.out.println("\n─── PATH FINDING ───\n");

        testPath(result, 0, 1);
        testPath(result, 0, 5);
        testPath(result, 2, 3);
    }

    private static void testPath(GraphAnalyzer.AnalysisResult result, int source, int dest) {
        Optional<List<Integer>> path = result.findPath(source, dest);
        if (path.isPresent()) {
            System.out.println("Path " + source + "→" + dest + ": " + formatPath(path.get()) +
                    " (distance: " + result.getDistance(source, dest) + ")");
        } else {
            System.out.println("No path from " + source + " to " + dest);
        }
    }

    private static Graph buildSimpleGraph() {
        return new GraphBuilder(6)
                .addEdge(1, 2)
                .addEdge(0, 3)
                .addEdge(2, 0)
                .addEdge(5, 4)
                .build();
    }

    private static Graph buildDisconnectedGraph() {
        return new GraphBuilder(5)
                .addEdge(0, 1)
                .addEdge(1, 2)
                .addEdge(3, 4)
                .build();
    }

    private static String formatPath(List<Integer> path) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < path.size(); i++) {
            sb.append(path.get(i));
            if (i < path.size() - 1) {
                sb.append(" → ");
            }
        }
        return sb.toString();
    }
}
