final class KruskalMST {
    private static final MinimumSpanningTreeSolver SOLVER = new KruskalMinimumSpanningTreeSolver();

    private KruskalMST() {
        // Compatibility facade.
    }

    public static int kruskalsMST(int vertexCount, int[][] rawEdges) {
        return SOLVER.minimumSpanningTreeCost(GraphFactory.fromEdgeMatrix(vertexCount, rawEdges));
    }

    static int minimumSpanningTreeCost(Graph graph) {
        return SOLVER.minimumSpanningTreeCost(graph);
    }

    static MinimumSpanningTreeResult findMinimumSpanningTree(Graph graph) {
        return SOLVER.findMinimumSpanningTree(graph);
    }
}
