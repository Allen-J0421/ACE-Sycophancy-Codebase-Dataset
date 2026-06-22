package mst;

public interface MinimumSpanningTreeSolver {
    MinimumSpanningTreeResult findMinimumSpanningTree(Graph graph);

    default int minimumSpanningTreeCost(Graph graph) {
        return findMinimumSpanningTree(graph).totalWeight();
    }
}
