/**
 * Demonstrates {@link FloydWarshall} on a small sample graph and prints the
 * resulting all-pairs shortest-path matrix.
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

        Graph shortest = FloydWarshall.shortestPaths(graph);
        printMatrix(shortest);
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
}
