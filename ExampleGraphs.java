final class ExampleGraphs {
    private static final int INF = FloydWarshall.NO_PATH;

    private ExampleGraphs() {
    }

    static int[][] weightedDirectedGraph() {
        return new int[][] {
            {0, 4, INF, 5, INF},
            {INF, 0, 1, INF, 6},
            {2, INF, 0, 3, INF},
            {INF, INF, 1, 0, 2},
            {1, INF, INF, 4, 0}
        };
    }

    static int[][] weightedDirectedGraphShortestPaths() {
        return new int[][] {
            {0, 4, 5, 5, 7},
            {3, 0, 1, 4, 6},
            {2, 6, 0, 3, 5},
            {3, 7, 1, 0, 2},
            {1, 5, 5, 4, 0}
        };
    }
}
