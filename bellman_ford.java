import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A single directed, weighted edge {@code from -> to} carrying an integer weight.
 *
 * <p>Weights may be negative; Bellman-Ford is specifically chosen over Dijkstra
 * to support that case.
 */
record WeightedEdge(int from, int to, int weight) {

    static WeightedEdge of(int from, int to, int weight) {
        return new WeightedEdge(from, to, weight);
    }
}

/**
 * An immutable directed, weighted graph over vertices {@code 0 .. vertices()-1}.
 *
 * <p>Construction validates the vertex count and that every edge endpoint refers
 * to a vertex that actually exists, so the algorithm never has to defend against
 * malformed input.
 */
final class WeightedGraph {

    private final int vertices;
    private final List<WeightedEdge> edges;

    private WeightedGraph(int vertices, List<WeightedEdge> edges) {
        this.vertices = vertices;
        this.edges = Collections.unmodifiableList(edges);
    }

    /** Builds a graph from a {@code {from, to, weight}} adjacency table. */
    static WeightedGraph from(int vertices, int[][] edgeData) {
        List<WeightedEdge> edges = new ArrayList<>(edgeData.length);
        for (int[] row : edgeData) {
            if (row.length != 3) {
                throw new IllegalArgumentException(
                    "each edge must be {from, to, weight}, got length " + row.length);
            }
            edges.add(WeightedEdge.of(row[0], row[1], row[2]));
        }
        return create(vertices, edges);
    }

    /** Builds a graph from explicit {@link WeightedEdge} values. */
    static WeightedGraph of(int vertices, WeightedEdge... edges) {
        return create(vertices, new ArrayList<>(Arrays.asList(edges)));
    }

    private static WeightedGraph create(int vertices, List<WeightedEdge> edges) {
        if (vertices <= 0) {
            throw new IllegalArgumentException("vertices must be positive, got " + vertices);
        }
        for (WeightedEdge edge : edges) {
            requireVertex(edge.from(), vertices, "from");
            requireVertex(edge.to(), vertices, "to");
        }
        return new WeightedGraph(vertices, edges);
    }

    private static void requireVertex(int vertex, int vertices, String role) {
        if (vertex < 0 || vertex >= vertices) {
            throw new IllegalArgumentException(
                "edge " + role + " vertex " + vertex + " out of range [0, " + vertices + ")");
        }
    }

    int vertices() {
        return vertices;
    }

    List<WeightedEdge> edges() {
        return edges;
    }
}

/**
 * The outcome of a shortest-path computation.
 *
 * <p>A run is either a {@linkplain #success success} (carrying the distance from
 * the source to every vertex) or a {@linkplain #negativeCycle negative cycle}
 * detection. Modelling these as distinct states removes the original
 * "{@code return new int[]{-1}}" sentinel, which callers could not reliably tell
 * apart from a genuine distance array.
 */
final class ShortestPathResult {

    /** Sentinel distance for a vertex that cannot be reached from the source. */
    static final int UNREACHABLE = Integer.MAX_VALUE;

    private final boolean negativeCycle;
    private final int[] distances;

    private ShortestPathResult(boolean negativeCycle, int[] distances) {
        this.negativeCycle = negativeCycle;
        this.distances = distances;
    }

    static ShortestPathResult success(int[] distances) {
        return new ShortestPathResult(false, distances.clone());
    }

    static ShortestPathResult negativeCycle() {
        return new ShortestPathResult(true, null);
    }

    boolean hasNegativeCycle() {
        return negativeCycle;
    }

    /** Whether {@code vertex} is reachable from the source. */
    boolean isReachable(int vertex) {
        return distance(vertex) != UNREACHABLE;
    }

    /**
     * The shortest distance to {@code vertex}.
     *
     * @return the distance, or {@link #UNREACHABLE} if it cannot be reached
     */
    int distanceTo(int vertex) {
        return distance(vertex);
    }

    private int distance(int vertex) {
        requireSuccess();
        return distances[vertex];
    }

    /** A defensive copy of all distances; index {@code i} is the distance to vertex {@code i}. */
    int[] distances() {
        requireSuccess();
        return distances.clone();
    }

    int vertexCount() {
        requireSuccess();
        return distances.length;
    }

    private void requireSuccess() {
        if (negativeCycle) {
            throw new IllegalStateException(
                "no distances are defined: the graph contains a negative-weight cycle");
        }
    }

    @Override
    public String toString() {
        if (negativeCycle) {
            return "ShortestPathResult[negative cycle]";
        }
        StringBuilder sb = new StringBuilder("ShortestPathResult[");
        for (int v = 0; v < distances.length; v++) {
            if (v > 0) {
                sb.append(", ");
            }
            sb.append(v).append('=')
              .append(distances[v] == UNREACHABLE ? "unreachable" : distances[v]);
        }
        return sb.append(']').toString();
    }
}

/**
 * Single-source shortest paths via the Bellman-Ford algorithm.
 *
 * <p>Bellman-Ford handles negative edge weights and reports negative-weight
 * cycles reachable from the source, neither of which Dijkstra's algorithm can do.
 * It runs in {@code O(V * E)} time.
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
        Arrays.fill(dist, ShortestPathResult.UNREACHABLE);
        dist[source] = 0;

        for (int pass = 0; pass < graph.vertices() - 1; pass++) {
            if (!relaxAll(graph, dist)) {
                break; // no edge improved this pass; further passes cannot either
            }
        }

        if (relaxAll(graph, dist)) {
            return ShortestPathResult.negativeCycle();
        }
        return ShortestPathResult.success(dist);
    }

    /**
     * Relaxes every edge once.
     *
     * @return {@code true} if any distance was reduced
     */
    private static boolean relaxAll(WeightedGraph graph, int[] dist) {
        boolean improved = false;
        for (WeightedEdge edge : graph.edges()) {
            int from = dist[edge.from()];
            if (from == ShortestPathResult.UNREACHABLE) {
                continue; // can't extend a path we haven't reached (also avoids overflow)
            }
            int candidate = from + edge.weight();
            if (candidate < dist[edge.to()]) {
                dist[edge.to()] = candidate;
                improved = true;
            }
        }
        return improved;
    }
}

/** Runnable demonstration of {@link BellmanFord} on a small sample graph. */
final class BellmanFordDemo {

    public static void main(String[] args) {
        WeightedGraph graph = WeightedGraph.from(5, new int[][] {
            {1, 3, 2},
            {4, 3, -1},
            {2, 4, 1},
            {1, 2, 1},
            {0, 1, 5}
        });

        ShortestPathResult result = BellmanFord.shortestPaths(graph, 0);

        if (result.hasNegativeCycle()) {
            System.out.println("Graph contains a negative-weight cycle.");
            return;
        }
        for (int v = 0; v < result.vertexCount(); v++) {
            System.out.print((result.isReachable(v) ? result.distanceTo(v) : "INF") + " ");
        }
        System.out.println();
    }
}
