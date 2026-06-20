package bipartite;

/**
 * An undirected edge connecting two <em>distinct</em> vertices. The order of the
 * endpoints is irrelevant: {@code new Edge(0, 1)} and {@code new Edge(1, 0)}
 * describe the same connection.
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
public record Edge(int u, int v) {

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
}
