final class ExampleGraphs {
    private static final int INF = FloydWarshall.NO_PATH;
    private static final int[][] WEIGHTED_DIRECTED_GRAPH = {
        {0, 4, INF, 5, INF},
        {INF, 0, 1, INF, 6},
        {2, INF, 0, 3, INF},
        {INF, INF, 1, 0, 2},
        {1, INF, INF, 4, 0}
    };
    private static final int[][] WEIGHTED_DIRECTED_GRAPH_SHORTEST_PATHS = {
        {0, 4, 5, 5, 7},
        {3, 0, 1, 4, 6},
        {2, 6, 0, 3, 5},
        {3, 7, 1, 0, 2},
        {1, 5, 5, 4, 0}
    };

    private ExampleGraphs() {
    }

    static int[][] weightedDirectedGraph() {
        return Matrices.copyOf(WEIGHTED_DIRECTED_GRAPH);
    }

    static int[][] weightedDirectedGraphShortestPaths() {
        return Matrices.copyOf(WEIGHTED_DIRECTED_GRAPH_SHORTEST_PATHS);
    }
}
