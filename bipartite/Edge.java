package bipartite;

import java.util.ArrayList;
import java.util.List;

/**
 * An unweighted edge connecting two <em>distinct</em> vertices. The meaning of
 * the endpoint order depends on the graph: irrelevant for an undirected graph,
 * source-to-target ({@code u -> v}) for a directed one.
 *
 * <p>Self-loops are rejected: this models a simple graph, which keeps the
 * bipartite check well-defined and guarantees that any odd-cycle witness has
 * length at least three. (A self-loop is trivially a length-one odd cycle, so a
 * graph containing one is never bipartite; callers that need that behaviour
 * should screen for self-loops before building the graph.)
 *
 * @param u one endpoint (a non-negative vertex id)
 * @param v the other endpoint (a non-negative vertex id, distinct from {@code u})
 */
public record Edge(int u, int v) implements GraphEdge {

    public Edge {
        if (u < 0 || v < 0) {
            throw new IllegalArgumentException(
                    "Vertex ids must be non-negative, got edge (" + u + ", " + v + ")");
        }
        if (u == v) {
            throw new IllegalArgumentException(
                    "Self-loops are not allowed, got edge (" + u + ", " + v + ")");
        }
    }

    /**
     * Converts an array of {@code {u, v}} pairs into a list of edges, matching
     * the classic competitive-programming input format. Shared by the
     * unweighted graph factories.
     *
     * @param pairs an array of two-element {@code {u, v}} arrays
     * @return the corresponding edges
     * @throws IllegalArgumentException if {@code pairs} is null, or any pair is
     *                                  null or does not have exactly two endpoints
     */
    public static List<Edge> fromPairs(int[][] pairs) {
        if (pairs == null) {
            throw new IllegalArgumentException("Edge array must not be null");
        }
        List<Edge> edges = new ArrayList<>(pairs.length);
        for (int[] pair : pairs) {
            if (pair == null || pair.length != 2) {
                throw new IllegalArgumentException("Each edge must have exactly two endpoints {u, v}");
            }
            edges.add(new Edge(pair[0], pair[1]));
        }
        return edges;
    }
}
