import graph.algorithm.BellmanFord;
import graph.algorithm.StronglyConnectedComponents;
import graph.algorithm.TopologicalSorter;
import graph.builder.AdvancedGraphBuilder;
import graph.model.DirectedGraph;
import graph.model.WeightedGraph;

import java.util.List;

public class Iteration7Demo {
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║  ITERATION 7: Directed Graphs & Advanced Analysis          ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");

        demonstrateDirectedGraphs();
        demonstrateSCC();
        demonstrateBellmanFord();
        demonstrateTopologicalSort();
        demonstrateAdvancedBuilders();
    }

    private static void demonstrateDirectedGraphs() {
        System.out.println("─── DIRECTED GRAPHS DEMONSTRATION ───\n");

        DirectedGraph graph = new DirectedGraph(5);
        graph.addDirectedEdge(0, 1, 2.0);
        graph.addDirectedEdge(0, 2, 4.0);
        graph.addDirectedEdge(1, 3, 1.0);
        graph.addDirectedEdge(2, 3, 3.0);
        graph.addDirectedEdge(3, 4, 2.0);

        System.out.println("Directed graph properties:");
        System.out.println("  Vertices: " + graph.getVertexCount());
        System.out.println("  Edges: " + graph.getEdgeCount());
        System.out.println("  Out-degree of 0: " + graph.getOutDegree(0));
        System.out.println("  In-degree of 3: " + graph.getInDegree(3));
        System.out.println();
    }

    private static void demonstrateSCC() {
        System.out.println("─── STRONGLY CONNECTED COMPONENTS ───\n");

        DirectedGraph graph = new DirectedGraph(6);
        graph.addDirectedEdge(0, 1, 1.0);
        graph.addDirectedEdge(1, 2, 1.0);
        graph.addDirectedEdge(2, 0, 1.0);
        graph.addDirectedEdge(2, 3, 1.0);
        graph.addDirectedEdge(3, 4, 1.0);
        graph.addDirectedEdge(4, 5, 1.0);
        graph.addDirectedEdge(5, 3, 1.0);

        StronglyConnectedComponents scc = new StronglyConnectedComponents(graph);
        System.out.println("Number of SCCs: " + scc.getComponentCount());

        List<List<Integer>> components = scc.getSCCs();
        for (int i = 0; i < components.size(); i++) {
            System.out.println("  SCC " + i + ": " + components.get(i));
        }
        System.out.println();
    }

    private static void demonstrateBellmanFord() {
        System.out.println("─── BELLMAN-FORD ALGORITHM (Negative Weights) ───\n");

        DirectedGraph graph = new DirectedGraph(5);
        graph.addDirectedEdge(0, 1, 4.0);
        graph.addDirectedEdge(0, 2, 2.0);
        graph.addDirectedEdge(1, 2, -3.0);
        graph.addDirectedEdge(2, 3, 2.0);
        graph.addDirectedEdge(2, 4, 4.0);
        graph.addDirectedEdge(3, 4, -5.0);

        BellmanFord bf = new BellmanFord(graph);
        bf.computeShortestPaths(0);

        System.out.println("Shortest paths from vertex 0:");
        for (int i = 0; i < graph.getVertexCount(); i++) {
            if (bf.hasPathTo(i)) {
                List<Integer> path = bf.getPath(i);
                System.out.print("  0 → " + i + ": distance=" + bf.getDistance(i) + ", path: ");
                for (int j = 0; j < path.size(); j++) {
                    System.out.print(path.get(j));
                    if (j < path.size() - 1) System.out.print(" → ");
                }
                System.out.println();
            }
        }
        System.out.println();
    }

    private static void demonstrateTopologicalSort() {
        System.out.println("─── TOPOLOGICAL SORT ───\n");

        DirectedGraph graph = new DirectedGraph(6);
        graph.addDirectedEdge(5, 2, 1.0);
        graph.addDirectedEdge(5, 0, 1.0);
        graph.addDirectedEdge(4, 0, 1.0);
        graph.addDirectedEdge(4, 1, 1.0);
        graph.addDirectedEdge(2, 3, 1.0);
        graph.addDirectedEdge(3, 1, 1.0);

        TopologicalSorter sorter = new TopologicalSorter(graph);
        List<Integer> order = sorter.sort();

        System.out.println("DFS-based topological sort: " + order);

        order = sorter.sortKahn();
        System.out.println("Kahn-based topological sort: " + order);
        System.out.println();
    }

    private static void demonstrateAdvancedBuilders() {
        System.out.println("─── ADVANCED GRAPH BUILDERS ───\n");

        DirectedGraph dag = AdvancedGraphBuilder.directedDAG(4, 4);
        System.out.println("Directed DAG: vertices=" + dag.getVertexCount() + ", edges=" + dag.getEdgeCount());

        DirectedGraph cycle = AdvancedGraphBuilder.directedCycle(5);
        System.out.println("Directed Cycle: vertices=" + cycle.getVertexCount() + ", edges=" + cycle.getEdgeCount());

        WeightedGraph complete = AdvancedGraphBuilder.completeWeightedGraph(4);
        System.out.println("Complete Weighted Graph: vertices=" + complete.getVertexCount() + ", edges=" + complete.getEdgeCount());

        WeightedGraph grid = AdvancedGraphBuilder.gridGraph(3, 3);
        System.out.println("Grid Graph (3x3): vertices=" + grid.getVertexCount() + ", edges=" + grid.getEdgeCount());
        System.out.println();
    }
}
