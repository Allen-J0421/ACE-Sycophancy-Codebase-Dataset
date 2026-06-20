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
        System.out.print(formatDistances(distances));
    }

    static String formatDistances(int[][] distances) {
        StringBuilder output = new StringBuilder();
        for (int[] row : distances) {
            for (int column = 0; column < row.length; column++) {
                if (column > 0) {
                    output.append(' ');
                }
                output.append(formatDistance(row[column]));
            }
            output.append(System.lineSeparator());
        }
        return output.toString();
    }

    private static String formatDistance(int distance) {
        if (FloydWarshall.isReachable(distance)) {
            return Integer.toString(distance);
        }
        return "INF";
    }
}
