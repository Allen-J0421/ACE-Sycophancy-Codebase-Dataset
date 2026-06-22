package demo;

import mst.MinimumSpanningTreeService;
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

        MinimumSpanningTreeResult result = new MinimumSpanningTreeService().findMinimumSpanningTree(4, edges);
        System.out.println(result.totalWeight());
        System.out.println(result.isConnected());
    }
}
