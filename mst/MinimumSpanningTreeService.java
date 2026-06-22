package mst;

public final class MinimumSpanningTreeService {
    private final KruskalMinimumSpanningTreeSolver solver;

    public MinimumSpanningTreeService() {
        this.solver = new KruskalMinimumSpanningTreeSolver();
    }

    public MinimumSpanningTreeResult findMinimumSpanningTree(Graph graph) {
        return solver.findMinimumSpanningTree(graph);
    }

    public int minimumSpanningTreeCost(Graph graph) {
        return solver.minimumSpanningTreeCost(graph);
    }
}
