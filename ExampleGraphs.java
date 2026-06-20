final class ExampleGraphs {
    private ExampleGraphs() {
    }

    static int[][] weightedDirectedGraph() {
        int inf = FloydWarshall.NO_PATH;
        return new int[][] {
            {0, 4, inf, 5, inf},
            {inf, 0, 1, inf, 6},
            {2, inf, 0, 3, inf},
            {inf, inf, 1, 0, 2},
            {1, inf, inf, 4, 0}
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
