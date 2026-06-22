package demo;

import mst.Graph;
import mst.GraphFactory;
import mst.KruskalMinimumSpanningTreeSolver;
import mst.MinimumSpanningTreeResult;

public final class KruskalMstDemo {
    private KruskalMstDemo() {
        // Demo entry point only.
    }

    public static void main(String[] args) {
        int[][] edges = {
            {0, 1, 10},
            {1, 3, 15},
            {2, 3, 4},
            {2, 0, 6},
            {0, 3, 5}
        };

        Graph graph = GraphFactory.fromEdgeMatrix(4, edges);
        MinimumSpanningTreeResult result = new KruskalMinimumSpanningTreeSolver().findMinimumSpanningTree(graph);
        System.out.println(result.totalWeight());
        System.out.println(result.isConnected());
    }
}
