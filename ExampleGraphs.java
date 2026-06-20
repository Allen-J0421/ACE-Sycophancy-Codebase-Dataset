final class ExampleGraphs {
    private ExampleGraphs() {
    }

    static int[][] weightedDirectedGraph() {
        return new int[][]{
                {0, 4, FloydWarshall.INF, 5, FloydWarshall.INF},
                {FloydWarshall.INF, 0, 1, FloydWarshall.INF, 6},
                {2, FloydWarshall.INF, 0, 3, FloydWarshall.INF},
                {FloydWarshall.INF, FloydWarshall.INF, 1, 0, 2},
                {1, FloydWarshall.INF, FloydWarshall.INF, 4, 0}
        };
    }

    static int[][] weightedDirectedGraphShortestPaths() {
        return new int[][]{
                {0, 4, 5, 5, 7},
                {3, 0, 1, 4, 6},
                {2, 6, 0, 3, 5},
                {3, 7, 1, 0, 2},
                {1, 5, 5, 4, 0}
        };
    }
}
