public final class FloydWarshallDemo {
    private FloydWarshallDemo() {
    }

    public static void main(String[] args) {
        int[][] graph = {
                {0, 4, FloydWarshall.INF, 5, FloydWarshall.INF},
                {FloydWarshall.INF, 0, 1, FloydWarshall.INF, 6},
                {2, FloydWarshall.INF, 0, 3, FloydWarshall.INF},
                {FloydWarshall.INF, FloydWarshall.INF, 1, 0, 2},
                {1, FloydWarshall.INF, FloydWarshall.INF, 4, 0}
        };

        int[][] distances = FloydWarshall.shortestPaths(graph);
        System.out.print(DistanceMatrixFormatter.format(distances));
    }
}
