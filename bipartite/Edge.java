package bipartite;

/**
 * An undirected edge connecting two vertices. The order of the endpoints is
 * irrelevant: {@code new Edge(0, 1)} and {@code new Edge(1, 0)} describe the
 * same connection.
 *
 * @param u one endpoint (a non-negative vertex id)
 * @param v the other endpoint (a non-negative vertex id)
 */
public record Edge(int u, int v) {

    public Edge {
        if (u < 0 || v < 0) {
            throw new IllegalArgumentException(
                    "Vertex ids must be non-negative, got edge (" + u + ", " + v + ")");
        }
    }
}
