import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class BellmanFord {

    // Half of MAX_VALUE so that dist[e.from()] + e.weight() never overflows.
    private static final int INF = Integer.MAX_VALUE / 2;

    private BellmanFord() {} // utility class — not instantiable

    /** Thrown when a negative-weight cycle is reachable from the source vertex. */
    public static class NegativeCycleException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public NegativeCycleException() {
            super("Graph contains a negative-weight cycle");
        }
    }

    public record Edge(int from, int to, int weight) {
        public Edge {
            if (from < 0) throw new IllegalArgumentException("from must be non-negative: " + from);
            if (to < 0) throw new IllegalArgumentException("to must be non-negative: " + to);
        }

        @Override
        public String toString() {
            return "(%d -[%d]-> %d)".formatted(from, to, weight);
        }
    }

    /** Returns true if distance was produced by shortestPaths (vs. the unreachable sentinel). */
    public static boolean isReachable(int distance) {
        return distance < INF;
    }

    /**
     * Returns shortest distances from src to all vertices.
     * Unreachable vertices carry a sentinel; test with {@link #isReachable}.
     *
     * @throws IllegalArgumentException if numVertices is non-positive, src is out of range,
     *                                  or any edge endpoint is outside [0, numVertices)
     * @throws NegativeCycleException   if a negative-weight cycle is reachable from src
     */
    public static int[] shortestPaths(int numVertices, Collection<Edge> edges, int src) {
        if (numVertices <= 0) throw new IllegalArgumentException("numVertices must be positive");
        if (src < 0 || src >= numVertices) throw new IllegalArgumentException("src out of range: " + src);
        Objects.requireNonNull(edges, "edges must not be null");
        for (Edge e : edges) {
            if (e.from() >= numVertices || e.to() >= numVertices) {
                throw new IllegalArgumentException("Edge endpoint out of range: " + e);
            }
        }

        int[] dist = new int[numVertices];
        Arrays.fill(dist, INF);
        dist[src] = 0;

        // V-1 passes guarantee shortest paths when no negative cycle exists:
        // the longest simple path in a V-vertex graph has at most V-1 edges.
        for (int i = 0; i < numVertices - 1; i++) {
            boolean relaxed = false;
            for (Edge e : edges) {
                relaxed |= relax(dist, e);
            }
            if (!relaxed) break; // converged early — no further passes can change anything
        }

        // A Vth relaxation succeeding means distances can still decrease, indicating a cycle.
        for (Edge e : edges) {
            if (canRelax(dist, e)) {
                throw new NegativeCycleException();
            }
        }

        return dist;
    }

    // Updates dist[e.to()] if e yields a shorter path; returns true if it did.
    private static boolean relax(int[] dist, Edge e) {
        if (canRelax(dist, e)) {
            dist[e.to()] = dist[e.from()] + e.weight();
            return true;
        }
        return false;
    }

    // Returns true if e would yield a shorter path without modifying dist.
    // Uses long arithmetic to avoid overflow when e.weight() is a large positive value.
    private static boolean canRelax(int[] dist, Edge e) {
        return dist[e.from()] < INF && (long) dist[e.from()] + e.weight() < dist[e.to()];
    }
}

class BellmanFordDemo {
    public static void main(String[] args) {
        // Vertex 5 is intentionally isolated to demonstrate the unreachable case.
        int numVertices = 6;
        List<BellmanFord.Edge> edges = List.of(
            new BellmanFord.Edge(1, 3, 2),
            new BellmanFord.Edge(4, 3, -1),
            new BellmanFord.Edge(2, 4, 1),
            new BellmanFord.Edge(1, 2, 1),
            new BellmanFord.Edge(0, 1, 5)
        );

        try {
            int[] distances = BellmanFord.shortestPaths(numVertices, edges, 0);
            for (int v = 0; v < distances.length; v++) {
                String dist = BellmanFord.isReachable(distances[v])
                    ? String.valueOf(distances[v])
                    : "unreachable";
                System.out.println("Vertex " + v + ": " + dist);
            }
        } catch (BellmanFord.NegativeCycleException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
