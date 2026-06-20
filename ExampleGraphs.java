final class ExampleGraphs {
    private ExampleGraphs() {
    }

    static int[][] weightedDirectedGraph() {
        return new int[][]{
                {0, 4, FloydWarshall.NO_PATH, 5, FloydWarshall.NO_PATH},
                {FloydWarshall.NO_PATH, 0, 1, FloydWarshall.NO_PATH, 6},
                {2, FloydWarshall.NO_PATH, 0, 3, FloydWarshall.NO_PATH},
                {FloydWarshall.NO_PATH, FloydWarshall.NO_PATH, 1, 0, 2},
                {1, FloydWarshall.NO_PATH, FloydWarshall.NO_PATH, 4, 0}
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
