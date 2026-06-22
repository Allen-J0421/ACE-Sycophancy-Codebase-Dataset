import java.util.List;

/**
 * Demonstrates {@link FloydWarshall}: prints the all-pairs shortest-path matrix
 * and reconstructs a few concrete shortest paths.
 */
public final class Main {

    public static void main(String[] args) {
        int inf = Graph.INF;

        Graph graph = Graph.of(new int[][] {
                {0,   4,   inf, 5,   inf},
                {inf, 0,   1,   inf, 6},
                {2,   inf, 0,   3,   inf},
                {inf, inf, 1,   0,   2},
                {1,   inf, inf, 4,   0}
        });

        ShortestPaths result = FloydWarshall.shortestPaths(graph);

        printMatrix(result.distances());

        System.out.println();
        printPath(result, 1, 0);
        printPath(result, 4, 1);
        printPath(result, 0, 4);
    }

    /** Prints the distance matrix, rendering {@link Graph#INF} as "INF". */
    private static void printMatrix(Graph graph) {
        int n = graph.size();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                int w = graph.weight(i, j);
                sb.append(w == Graph.INF ? "INF" : Integer.toString(w));
                sb.append(' ');
            }
            sb.append(System.lineSeparator());
        }
        System.out.print(sb);
    }

    /** Prints the shortest path and its distance between two vertices. */
    private static void printPath(ShortestPaths result, int from, int to) {
        List<Integer> path = result.path(from, to);
        if (path.isEmpty()) {
            System.out.printf("%d -> %d: unreachable%n", from, to);
        } else {
            System.out.printf("%d -> %d (distance %d): %s%n",
                    from, to, result.distance(from, to), path);
        }
    }
}
