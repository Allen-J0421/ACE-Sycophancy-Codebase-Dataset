/**
 * An edge between two vertices, with an identity that is unique within its graph.
 * The identity is what separates parallel edges from one another (which bridge
 * detection requires).
 *
 * <p>For an undirected edge the two endpoints are interchangeable. For a directed
 * arc, {@code u} is the tail (source) and {@code v} is the head (target).
 *
 * @param u  one endpoint (the tail, for a directed arc)
 * @param v  the other endpoint (the head, for a directed arc)
 * @param id the edge's unique identifier within its graph
 */
public record Edge(int u, int v, int id) {

    /**
     * Returns the endpoint of this edge other than {@code endpoint}.
     *
     * @param endpoint one endpoint of this edge
     * @return the opposite endpoint (equal to {@code endpoint} for a self-loop)
     * @throws IllegalArgumentException if {@code endpoint} is not an endpoint
     */
    public int other(int endpoint) {
        if (endpoint == u) {
            return v;
        }
        if (endpoint == v) {
            return u;
        }
        throw new IllegalArgumentException(endpoint + " is not an endpoint of " + this);
    }
}
