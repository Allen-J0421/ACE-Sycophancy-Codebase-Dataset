/**
 * Computes all-pairs shortest paths using the Floyd-Warshall algorithm.
 *
 * <p>The algorithm runs in {@code O(V^3)} time and {@code O(V^2)} space and
 * supports negative edge weights. It does not mutate its input: callers receive
 * a fresh {@link Graph} of shortest-path distances.
 */
public final class FloydWarshall {

    private FloydWarshall() {
        // Utility class; not instantiable.
    }

    /**
     * Returns a graph whose cells hold the shortest-path distance between every
     * pair of vertices.
     *
     * @param graph the input graph
     * @return shortest-path distances, with {@link Graph#INF} for unreachable pairs
     * @throws IllegalArgumentException if the graph contains a negative cycle,
     *                                  for which shortest paths are undefined
     */
    public static Graph shortestPaths(Graph graph) {
        int n = graph.size();
        int[][] dist = graph.toMatrix();

        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                // Skip the whole row when i cannot reach the pivot k.
                if (dist[i][k] == Graph.INF) {
                    continue;
                }
                for (int j = 0; j < n; j++) {
                    if (dist[k][j] == Graph.INF) {
                        continue;
                    }
                    int throughK = dist[i][k] + dist[k][j];
                    if (throughK < dist[i][j]) {
                        dist[i][j] = throughK;
                    }
                }
            }
        }

        detectNegativeCycle(dist);
        return Graph.of(dist);
    }

    /**
     * A negative diagonal entry means a vertex can reach itself with negative
     * total weight, i.e. the graph has a negative cycle.
     */
    private static void detectNegativeCycle(int[][] dist) {
        for (int i = 0; i < dist.length; i++) {
            if (dist[i][i] < 0) {
                throw new IllegalArgumentException(
                        "graph contains a negative cycle through vertex " + i);
            }
        }
    }
}
