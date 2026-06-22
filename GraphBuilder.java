import java.util.ArrayList;
import java.util.List;

/**
 * A fluent builder for {@link Graph}s.
 *
 * <p>It removes the need to declare the vertex count up front: the graph is sized
 * to the highest vertex referenced by any edge. An explicit minimum can still be
 * set with {@link #vertices(int)} — needed to include isolated vertices that no
 * edge touches. Choose {@link #undirected()} or {@link #directed()} to pick the
 * edge semantics.
 *
 * <p>It is a convenience layered on top of the constructors and {@code fromEdges}
 * factories, most useful for graphs built incrementally or programmatically; for a
 * small fixed graph, {@code UndirectedGraph.fromEdges(n, {{...}})} is still the
 * most compact option.
 *
 * <pre>{@code
 * Graph g = GraphBuilder.undirected().edge(0, 1).edge(1, 2).build();
 * Graph d = GraphBuilder.directed().edges(new int[][] {{0, 1}, {1, 2}}).vertices(5).build();
 * }</pre>
 */
public final class GraphBuilder {

    private final boolean directed;
    private final List<int[]> edges = new ArrayList<>();
    private int minVertexCount;

    private GraphBuilder(boolean directed) {
        this.directed = directed;
    }

    /** Starts building an undirected graph. */
    public static GraphBuilder undirected() {
        return new GraphBuilder(false);
    }

    /** Starts building a directed graph. */
    public static GraphBuilder directed() {
        return new GraphBuilder(true);
    }

    /**
     * Ensures the built graph has at least {@code count} vertices. Useful for
     * isolated vertices that no edge references.
     *
     * @param count the minimum number of vertices; must be non-negative
     * @return this builder
     */
    public GraphBuilder vertices(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("count must be non-negative: " + count);
        }
        minVertexCount = Math.max(minVertexCount, count);
        return this;
    }

    /**
     * Adds an edge. For a directed builder this is the arc {@code a -> b}; for an
     * undirected builder the endpoints are interchangeable. The graph grows to
     * include the larger endpoint.
     *
     * @param a one endpoint (the tail, for a directed builder); must be non-negative
     * @param b the other endpoint (the head, for a directed builder); must be non-negative
     * @return this builder
     */
    public GraphBuilder edge(int a, int b) {
        if (a < 0 || b < 0) {
            throw new IllegalArgumentException("vertices must be non-negative: " + a + ", " + b);
        }
        edges.add(new int[] {a, b});
        minVertexCount = Math.max(minVertexCount, Math.max(a, b) + 1);
        return this;
    }

    /**
     * Adds several edges at once, e.g. {@code edges(new int[][] {{0, 1}, {1, 2}})}.
     *
     * @param pairs an array of {@code {a, b}} endpoint pairs
     * @return this builder
     */
    public GraphBuilder edges(int[][] pairs) {
        for (int[] pair : pairs) {
            if (pair.length != 2) {
                throw new IllegalArgumentException(
                        "each edge must have exactly two endpoints, found " + pair.length);
            }
            edge(pair[0], pair[1]);
        }
        return this;
    }

    /**
     * Builds the graph. The builder may be reused or extended afterwards; each call
     * produces an independent graph from the edges accumulated so far.
     *
     * @return a new {@link UndirectedGraph} or {@link DirectedGraph} per the chosen
     *         {@link #undirected()}/{@link #directed()} mode
     */
    public Graph build() {
        if (directed) {
            DirectedGraph graph = new DirectedGraph(minVertexCount);
            for (int[] edge : edges) {
                graph.addEdge(edge[0], edge[1]);
            }
            return graph;
        }
        UndirectedGraph graph = new UndirectedGraph(minVertexCount);
        for (int[] edge : edges) {
            graph.addEdge(edge[0], edge[1]);
        }
        return graph;
    }
}
