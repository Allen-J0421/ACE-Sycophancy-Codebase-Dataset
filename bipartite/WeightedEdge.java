package bipartite;

/**
 * A weighted edge connecting two <em>distinct</em> vertices. As with
 * {@link Edge}, the endpoint order is irrelevant for an undirected graph and
 * denotes source-to-target ({@code u -> v}) for a directed one. The weight is an
 * arbitrary {@code double} (it may be negative); interpreting it is the caller's
 * concern.
 *
 * @param u      one endpoint (a non-negative vertex id)
 * @param v      the other endpoint (a non-negative vertex id, distinct from {@code u})
 * @param weight the weight associated with the edge
 */
public record WeightedEdge(int u, int v, double weight) implements GraphEdge {

    public WeightedEdge {
        if (u < 0 || v < 0) {
            throw new IllegalArgumentException(
                    "Vertex ids must be non-negative, got edge (" + u + ", " + v + ")");
        }
        if (u == v) {
            throw new IllegalArgumentException(
                    "Self-loops are not allowed, got edge (" + u + ", " + v + ")");
        }
        if (Double.isNaN(weight)) {
            throw new IllegalArgumentException("Edge weight must not be NaN");
        }
    }
}
