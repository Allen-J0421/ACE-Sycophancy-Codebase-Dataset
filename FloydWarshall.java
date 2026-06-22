/**
 * Computes all-pairs shortest paths using the Floyd-Warshall algorithm.
 *
 * <p>The algorithm runs in {@code O(V^3)} time and {@code O(V^2)} space and
 * supports negative edge weights. It does not mutate its input: callers receive
 * a fresh {@link ShortestPaths} carrying both distances and path-reconstruction
 * data.
 */
public final class FloydWarshall {

    private FloydWarshall() {
        // Utility class; not instantiable.
    }

    /**
     * Computes shortest-path distances and paths between every pair of vertices.
     *
     * @param graph the input graph
     * @return distances (with {@link Graph#INF} for unreachable pairs) and the
     *         means to reconstruct the actual shortest paths
     * @throws IllegalArgumentException if the graph contains a negative cycle,
     *                                  for which shortest paths are undefined
     */
    public static ShortestPaths shortestPaths(Graph graph) {
        int n = graph.size();
        int[][] dist = graph.toMatrix();
        int[][] next = initialNextHops(graph, dist);

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
                        next[i][j] = next[i][k];
                    }
                }
            }
        }

        detectNegativeCycle(dist);
        return new ShortestPaths(dist, next);
    }

    /**
     * Seeds the next-hop matrix from the direct edges: the first hop from
     * {@code i} toward {@code j} is {@code j} itself when a direct edge exists.
     */
    private static int[][] initialNextHops(Graph graph, int[][] dist) {
        int n = graph.size();
        int[][] next = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                next[i][j] = (i != j && dist[i][j] != Graph.INF)
                        ? j
                        : ShortestPaths.NO_PATH;
            }
        }
        return next;
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
