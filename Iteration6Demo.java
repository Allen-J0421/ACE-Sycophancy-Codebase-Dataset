import graph.algorithm.Dijkstra;
import graph.algorithm.Kruskal;
import graph.analysis.CentralityAnalyzer;
import graph.builder.GraphGenerator;
import graph.core.Graph;
import graph.model.Edge;
import graph.model.WeightedGraph;

import java.util.List;
import java.util.Map;

public class Iteration6Demo {
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║     ITERATION 6: Weighted Graphs & Advanced Algorithms      ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");

        demonstrateWeightedGraphs();
        demonstrateDijkstra();
        demonstrateKruskal();
        demonstrateGraphGenerators();
        demonstrateCentrality();
    }

    private static void demonstrateWeightedGraphs() {
        System.out.println("─── WEIGHTED GRAPHS DEMONSTRATION ───\n");

        WeightedGraph graph = new WeightedGraph(5);
        graph.addWeightedEdge(0, 1, 4.0);
        graph.addWeightedEdge(1, 2, 2.0);
        graph.addWeightedEdge(2, 3, 5.0);
        graph.addWeightedEdge(3, 4, 1.0);
        graph.addWeightedEdge(0, 4, 8.0);

        System.out.println("Created weighted graph with edges:");
        for (Edge edge : graph.getAllEdges()) {
            System.out.println("  " + edge.getSource() + " → " + edge.getDestination() + " (weight: " + edge.getWeight() + ")");
        }

        System.out.println("Total edges: " + graph.getEdgeCount());
        System.out.println("Total weight: " + graph.getAllEdges().stream().mapToDouble(Edge::getWeight).sum() + "\n");
    }

    private static void demonstrateDijkstra() {
        System.out.println("─── DIJKSTRA'S SHORTEST PATH ALGORITHM ───\n");

        WeightedGraph graph = new WeightedGraph(6);
        graph.addWeightedEdge(0, 1, 4.0);
        graph.addWeightedEdge(0, 2, 2.0);
        graph.addWeightedEdge(1, 2, 1.0);
        graph.addWeightedEdge(1, 3, 5.0);
        graph.addWeightedEdge(2, 3, 8.0);
        graph.addWeightedEdge(2, 4, 10.0);
        graph.addWeightedEdge(3, 4, 2.0);
        graph.addWeightedEdge(4, 5, 3.0);

        Dijkstra dijkstra = new Dijkstra(graph);
        dijkstra.computeShortestPaths(0);

        System.out.println("Shortest paths from vertex 0:\n");
        for (int i = 0; i < graph.getVertexCount(); i++) {
            if (dijkstra.hasPathTo(i)) {
                List<Integer> path = dijkstra.getPath(i);
                System.out.print("  0 → " + i + ": distance=" + dijkstra.getDistance(i) + ", path: ");
                for (int j = 0; j < path.size(); j++) {
                    System.out.print(path.get(j));
                    if (j < path.size() - 1) System.out.print(" → ");
                }
                System.out.println();
            }
        }
        System.out.println();
    }

    private static void demonstrateKruskal() {
        System.out.println("─── KRUSKAL'S MINIMUM SPANNING TREE ───\n");

        WeightedGraph graph = new WeightedGraph(5);
        graph.addWeightedEdge(0, 1, 2.0);
        graph.addWeightedEdge(0, 3, 6.0);
        graph.addWeightedEdge(1, 2, 3.0);
        graph.addWeightedEdge(1, 3, 8.0);
        graph.addWeightedEdge(1, 4, 5.0);
        graph.addWeightedEdge(2, 4, 7.0);
        graph.addWeightedEdge(3, 4, 9.0);

        Kruskal kruskal = new Kruskal(graph);
        List<Edge> mst = kruskal.findMST();
        double totalWeight = kruskal.getMSTWeight();

        System.out.println("Minimum Spanning Tree edges:");
        for (Edge edge : mst) {
            System.out.println("  " + edge.getSource() + " — " + edge.getDestination() + " (weight: " + edge.getWeight() + ")");
        }
        System.out.println("Total MST weight: " + totalWeight + "\n");
    }

    private static void demonstrateGraphGenerators() {
        System.out.println("─── GRAPH GENERATOR DEMONSTRATION ───\n");

        System.out.println("Complete Graph (K5):");
        Graph complete = GraphGenerator.completeGraph(5);
        System.out.println("  Vertices: " + complete.getVertexCount() + ", Edges: " + complete.getEdgeCount());

        System.out.println("\nCycle Graph (C6):");
        Graph cycle = GraphGenerator.cycleGraph(6);
        System.out.println("  Vertices: " + cycle.getVertexCount() + ", Edges: " + cycle.getEdgeCount());

        System.out.println("\nPath Graph (P5):");
        Graph path = GraphGenerator.pathGraph(5);
        System.out.println("  Vertices: " + path.getVertexCount() + ", Edges: " + path.getEdgeCount());

        System.out.println("\nBipartite Graph (K3,3):");
        Graph bipartite = GraphGenerator.bipartiteGraph(3, 3);
        System.out.println("  Vertices: " + bipartite.getVertexCount() + ", Edges: " + bipartite.getEdgeCount());

        System.out.println("\nWheel Graph (W5):");
        Graph wheel = GraphGenerator.wheelGraph(5);
        System.out.println("  Vertices: " + wheel.getVertexCount() + ", Edges: " + wheel.getEdgeCount() + "\n");
    }

    private static void demonstrateCentrality() {
        System.out.println("─── CENTRALITY ANALYSIS DEMONSTRATION ───\n");

        Graph star = new graph.core.GraphBuilder(5)
                .addEdge(0, 1)
                .addEdge(0, 2)
                .addEdge(0, 3)
                .addEdge(0, 4)
                .build();

        CentralityAnalyzer analyzer = new CentralityAnalyzer(star);

        System.out.println("Star graph centrality analysis:\n");
        System.out.println("Degree Centrality:");
        Map<Integer, Double> degreeCentrality = analyzer.getAllDegreeCentralities();
        for (int i = 0; i < 5; i++) {
            System.out.println("  Vertex " + i + ": " + String.format("%.4f", degreeCentrality.get(i)));
        }

        System.out.println("\nCloseness Centrality:");
        Map<Integer, Double> closenessCentrality = analyzer.getAllClosenessCentralities();
        for (int i = 0; i < 5; i++) {
            System.out.println("  Vertex " + i + ": " + String.format("%.4f", closenessCentrality.get(i)));
        }

        System.out.println("\nMost central vertex by degree: " + analyzer.getMostCentralVertexByDegree());
        System.out.println("Most central vertex by closeness: " + analyzer.getMostCentralVertexByCloseness() + "\n");
    }
}
