import java.util.List;

/**
 * Demonstrates {@link FloydWarshall}: prints the all-pairs shortest-path matrix
 * and reconstructs a few concrete shortest paths.
 */
public final class Main {

    public static void main(String[] args) {
        Graph graph = Graph.builder(5)
                .addEdge(0, 1, 4)
                .addEdge(0, 3, 5)
                .addEdge(1, 2, 1)
                .addEdge(1, 4, 6)
                .addEdge(2, 0, 2)
                .addEdge(2, 3, 3)
                .addEdge(3, 2, 1)
                .addEdge(3, 4, 2)
                .addEdge(4, 0, 1)
                .addEdge(4, 3, 4)
                .build();

        ShortestPaths result = FloydWarshall.shortestPaths(graph);

        printMatrix(result.distances());

        System.out.println();
        for (int v = 0; v < graph.size(); v++) {
            System.out.printf("neighbors of %d: %s%n", v, graph.neighbors(v));
        }

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
