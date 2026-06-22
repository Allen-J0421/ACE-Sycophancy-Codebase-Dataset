package demo;

import mst.Graph;
import mst.Edge;
import mst.MinimumSpanningTreeService;
import mst.MinimumSpanningTreeResult;

public final class KruskalMstDemo {
    private KruskalMstDemo() {
        // Demo entry point only.
    }

    public static void main(String[] args) {
        Graph graph = Graph.of(
            4,
            new Edge(0, 1, 10),
            new Edge(1, 3, 15),
            new Edge(2, 3, 4),
            new Edge(2, 0, 6),
            new Edge(0, 3, 5)
        );
        MinimumSpanningTreeResult result = new MinimumSpanningTreeService().findMinimumSpanningTree(graph);
        System.out.println(result.totalWeight());
        System.out.println(result.isConnected());
    }
}
