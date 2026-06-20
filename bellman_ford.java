import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

    /** Sentinel for "no vertex", e.g. a pass that relaxed nothing. */
    private static final int NO_VERTEX = -1;

    private BellmanFord() {
    }

    /**
     * Computes the shortest distance from {@code source} to every vertex.
     *
     * <p>The classic algorithm: relax every edge {@code V - 1} times, then run one
     * additional pass. If any edge can still be relaxed on that final pass, a
     * negative-weight cycle is reachable; the cycle itself is then reconstructed
     * from the predecessor chain.
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
        // enabling path (and negative-cycle) reconstruction; the source and
        // unreachable vertices have none.
        int[] predecessors = new int[graph.vertices()];
        Arrays.fill(predecessors, Distances.NO_PREDECESSOR);

        for (int pass = 0; pass < graph.vertices() - 1; pass++) {
            if (relaxPass(graph, dist, predecessors) == NO_VERTEX) {
                break; // no edge improved this pass; further passes cannot either
            }
        }

        // One more pass: any vertex still improving lies on or downstream of a cycle.
        int affected = relaxPass(graph, dist, predecessors);
        if (affected != NO_VERTEX) {
            return new NegativeCycle(extractCycle(affected, predecessors, graph.vertices()));
        }
        return new Distances(source, dist, predecessors);
    }

    /**
     * Relaxes every edge once, recording the predecessor for any improved vertex.
     *
     * @return the last vertex whose distance was reduced, or {@link #NO_VERTEX} if none
     */
    private static int relaxPass(WeightedGraph graph, int[] dist, int[] predecessors) {
        int improved = NO_VERTEX;
        for (int from = 0; from < graph.vertices(); from++) {
            int distFrom = dist[from];
            if (distFrom == Distances.UNREACHABLE) {
                continue; // can't extend a path we haven't reached (also avoids overflow)
            }
            for (WeightedGraph.Arc arc : graph.outgoing(from)) {
                int candidate = distFrom + arc.weight();
                if (candidate < dist[arc.to()]) {
                    dist[arc.to()] = candidate;
                    predecessors[arc.to()] = from;
                    improved = arc.to();
                }
            }
        }
        return improved;
    }

    /**
     * Reconstructs a negative-weight cycle from the predecessor chain.
     *
     * @param affected a vertex relaxed on the detection pass (on or downstream of the cycle)
     * @return the cycle vertices in traversal order: edge {@code v[i] -> v[i+1]} exists,
     *     and the last vertex closes back to the first
     */
    private static List<Integer> extractCycle(int affected, int[] predecessors, int vertices) {
        // Stepping back V times is guaranteed to land on a vertex of the cycle itself,
        // even if `affected` only leads into the cycle rather than lying on it.
        int onCycle = affected;
        for (int i = 0; i < vertices; i++) {
            onCycle = predecessors[onCycle];
        }
        // Walk predecessors once around the loop, then reverse into traversal order.
        List<Integer> cycle = new ArrayList<>();
        int v = onCycle;
        do {
            cycle.add(v);
            v = predecessors[v];
        } while (v != onCycle);
        Collections.reverse(cycle);
        return cycle;
    }
}
