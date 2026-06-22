package mst;

public final class MinimumSpanningTreeService {
    private final KruskalMinimumSpanningTreeSolver solver;

    public MinimumSpanningTreeService() {
        this.solver = new KruskalMinimumSpanningTreeSolver();
    }

    public MinimumSpanningTreeResult findMinimumSpanningTree(Graph graph) {
        return solver.findMinimumSpanningTree(graph);
    }

    public MinimumSpanningTreeResult findMinimumSpanningTree(int vertexCount, int[][] rawEdges) {
        return findMinimumSpanningTree(GraphFactory.fromEdgeMatrix(vertexCount, rawEdges));
    }

    public int minimumSpanningTreeCost(Graph graph) {
        return solver.minimumSpanningTreeCost(graph);
    }

    public int minimumSpanningTreeCost(int vertexCount, int[][] rawEdges) {
        return minimumSpanningTreeCost(GraphFactory.fromEdgeMatrix(vertexCount, rawEdges));
    }
}
