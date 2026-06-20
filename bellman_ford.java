import java.util.Arrays;

/**
 * Single-source shortest paths via the Bellman-Ford algorithm.
 *
 * <p>Bellman-Ford handles negative edge weights and reports negative-weight
 * cycles reachable from the source, neither of which Dijkstra's algorithm can do.
 * It runs in {@code O(V * E)} time.
 *
 * <p>The result is returned as a {@link ShortestPathResult}: either
 * {@link Distances} or {@link NegativeCycle}.
 */
final class BellmanFord {

    private BellmanFord() {
    }

    /**
     * Computes the shortest distance from {@code source} to every vertex.
     *
     * <p>The classic algorithm: relax every edge {@code V - 1} times, then run one
     * additional pass. If any edge can still be relaxed on that final pass, a
     * negative-weight cycle is reachable and no shortest path is well defined.
     */
    static ShortestPathResult shortestPaths(WeightedGraph graph, int source) {
        if (source < 0 || source >= graph.vertices()) {
            throw new IllegalArgumentException(
                "source vertex " + source + " out of range [0, " + graph.vertices() + ")");
        }

        int[] dist = new int[graph.vertices()];
        Arrays.fill(dist, Distances.UNREACHABLE);
        dist[source] = 0;

        // predecessors[v] is the vertex preceding v on the best known path to v,
        // enabling path reconstruction; the source and unreachable vertices have none.
        int[] predecessors = new int[graph.vertices()];
        Arrays.fill(predecessors, Distances.NO_PREDECESSOR);

        for (int pass = 0; pass < graph.vertices() - 1; pass++) {
            if (!relaxAll(graph, dist, predecessors)) {
                break; // no edge improved this pass; further passes cannot either
            }
        }

        if (relaxAll(graph, dist, predecessors)) {
            return new NegativeCycle();
        }
        return new Distances(source, dist, predecessors);
    }

    /**
     * Relaxes every edge once, recording the predecessor for any improved vertex.
     *
     * @return {@code true} if any distance was reduced
     */
    private static boolean relaxAll(WeightedGraph graph, int[] dist, int[] predecessors) {
        boolean improved = false;
        for (WeightedEdge edge : graph.edges()) {
            int from = dist[edge.from()];
            if (from == Distances.UNREACHABLE) {
                continue; // can't extend a path we haven't reached (also avoids overflow)
            }
            int candidate = from + edge.weight();
            if (candidate < dist[edge.to()]) {
                dist[edge.to()] = candidate;
                predecessors[edge.to()] = edge.from();
                improved = true;
            }
        }
        return improved;
    }
}
