package graph.demo;

import graph.analysis.BiconnectedComponentsAnalyzer;
import graph.analysis.BridgesAndArticulationFinder;
import graph.builder.GraphGenerator;
import graph.core.Graph;
import graph.core.GraphBuilder;
import graph.metrics.AdvancedMetrics;

public class Iteration8Demo {
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║          ITERATION 8: ADVANCED CONNECTIVITY & METRICS         ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝\n");

        demonstrateBridges();
        demonstrateArticulationPoints();
        demonstrateBiconnectedComponents();
        demonstrateAdvancedMetrics();
    }

    private static void demonstrateBridges() {
        System.out.println("┌─ BRIDGES ANALYSIS ─────────────────────────────────────────────┐\n");

        Graph graph = new GraphBuilder(7)
                .addEdge(0, 1)
                .addEdge(1, 2)
                .addEdge(1, 3)
                .addEdge(2, 3)
                .addEdge(3, 4)
                .addEdge(4, 5)
                .addEdge(4, 6)
                .addEdge(5, 6)
                .build();

        BridgesAndArticulationFinder finder = new BridgesAndArticulationFinder(graph);

        System.out.println("Graph: 7 vertices, 8 edges");
        System.out.println("Found bridges (critical edges):");
        for (var bridge : finder.getBridges()) {
            System.out.println("  • Edge (" + bridge.getSource() + ", " + bridge.getDestination() + ")");
        }
        System.out.println("\nBridges are edges whose removal disconnects the graph.\n");
    }

    private static void demonstrateArticulationPoints() {
        System.out.println("┌─ ARTICULATION POINTS ANALYSIS ─────────────────────────────────┐\n");

        Graph graph = new GraphBuilder(6)
                .addEdge(0, 1)
                .addEdge(1, 2)
                .addEdge(2, 3)
                .addEdge(3, 4)
                .addEdge(4, 5)
                .build();

        BridgesAndArticulationFinder finder = new BridgesAndArticulationFinder(graph);

        System.out.println("Graph: Linear path 0-1-2-3-4-5");
        System.out.println("Articulation points (critical vertices):");
        for (int point : finder.getArticulationPoints()) {
            System.out.println("  • Vertex " + point);
        }
        System.out.println("\nArticulation points are vertices whose removal increases components.\n");
    }

    private static void demonstrateBiconnectedComponents() {
        System.out.println("┌─ BICONNECTED COMPONENTS ANALYSIS ──────────────────────────────┐\n");

        Graph graph = new GraphBuilder(8)
                .addEdge(0, 1)
                .addEdge(1, 2)
                .addEdge(0, 2)
                .addEdge(2, 3)
                .addEdge(3, 4)
                .addEdge(3, 5)
                .addEdge(4, 5)
                .addEdge(5, 6)
                .addEdge(5, 7)
                .addEdge(6, 7)
                .build();

        BiconnectedComponentsAnalyzer analyzer = new BiconnectedComponentsAnalyzer(graph);

        System.out.println("Graph: Complex structure with cycles and bridges");
        System.out.println("Biconnected components: " + analyzer.getComponentCount());
        System.out.println("Articulation points: " + analyzer.getArticulationPoints().size());
        System.out.println("\nComponents:");
        for (int i = 0; i < analyzer.getComponents().size(); i++) {
            System.out.println("  Component " + (i + 1) + ": " + analyzer.getComponents().get(i));
        }
        System.out.println("\nBiconnected components are maximal subgraphs with no articulation points.\n");
    }

    private static void demonstrateAdvancedMetrics() {
        System.out.println("┌─ ADVANCED METRICS ANALYSIS ────────────────────────────────────┐\n");

        System.out.println("1. Clustering Coefficient (Complete Graph K₅):");
        Graph k5 = GraphGenerator.completeGraph(5);
        AdvancedMetrics metrics1 = new AdvancedMetrics(k5);
        System.out.println(metrics1.getReport());

        System.out.println("2. Average Path Length (Cycle Graph C₆):");
        Graph c6 = GraphGenerator.cycleGraph(6);
        AdvancedMetrics metrics2 = new AdvancedMetrics(c6);
        System.out.println(metrics2.getReport());

        System.out.println("3. Assortativity (Path Graph P₁₀):");
        Graph p10 = GraphGenerator.pathGraph(10);
        AdvancedMetrics metrics3 = new AdvancedMetrics(p10);
        System.out.println(metrics3.getReport());

        System.out.println("Metric Interpretations:");
        System.out.println("  • Clustering Coefficient: How likely neighbors of a vertex connect");
        System.out.println("  • Average Path Length: Mean shortest path between all pairs");
        System.out.println("  • Assortativity: Tendency of high-degree vertices to connect\n");
    }
}
