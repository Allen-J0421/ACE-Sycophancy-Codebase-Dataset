import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An undirected graph backed by an adjacency list.
 *
 * <p>Vertices are identified by integers in the range {@code [0, vertexCount())}.
 * Each edge carries a stable identity (see {@link Edge}), so parallel edges
 * between the same pair of vertices are distinguishable — which algorithms such as
 * bridge detection require.
 */
public final class Graph {

    /**
     * An undirected edge between two vertices, with an identity that is unique
     * within its graph. The identity is what separates parallel edges from one
     * another.
     *
     * @param u  one endpoint
     * @param v  the other endpoint
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

    /** For each vertex, the edges incident to it. */
    private final List<List<Edge>> incidence;

    /** All edges, in insertion order; an edge's index equals its {@link Edge#id()}. */
    private final List<Edge> edges = new ArrayList<>();

    /**
     * Creates a graph with the given number of vertices and no edges.
     *
     * @param vertexCount the number of vertices; must be non-negative
     */
    public Graph(int vertexCount) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("vertexCount must be non-negative: " + vertexCount);
        }
        incidence = new ArrayList<>(vertexCount);
        for (int i = 0; i < vertexCount; i++) {
            incidence.add(new ArrayList<>());
        }
    }

    /**
     * Builds a graph from a list of undirected edges.
     *
     * @param vertexCount the number of vertices
     * @param edges       an array of {@code {u, v}} pairs
     * @return the constructed graph
     */
    public static Graph fromEdges(int vertexCount, int[][] edges) {
        Graph graph = new Graph(vertexCount);
        for (int[] edge : edges) {
            if (edge.length != 2) {
                throw new IllegalArgumentException(
                        "each edge must have exactly two endpoints, found " + edge.length);
            }
            graph.addEdge(edge[0], edge[1]);
        }
        return graph;
    }

    /**
     * Adds an undirected edge between two vertices. Parallel edges and self-loops
     * are permitted; each call adds a distinct edge.
     *
     * @param u one endpoint
     * @param v the other endpoint
     * @return the newly created edge
     */
    public Edge addEdge(int u, int v) {
        checkVertex(u);
        checkVertex(v);
        Edge edge = new Edge(u, v, edges.size());
        edges.add(edge);
        incidence.get(u).add(edge);
        if (u != v) {
            incidence.get(v).add(edge);
        }
        return edge;
    }

    /** Returns the number of vertices in the graph. */
    public int vertexCount() {
        return incidence.size();
    }

    /** Returns the number of edges in the graph. */
    public int edgeCount() {
        return edges.size();
    }

    /**
     * Returns an unmodifiable view of the edges incident to {@code u}. From each
     * edge, {@link Edge#other(int) edge.other(u)} gives the adjacent vertex.
     *
     * @param u the vertex whose incident edges are requested
     * @return the edges incident to {@code u}
     */
    public List<Edge> incidentEdges(int u) {
        checkVertex(u);
        return Collections.unmodifiableList(incidence.get(u));
    }

    /** Returns an unmodifiable view of all edges in the graph. */
    public List<Edge> edges() {
        return Collections.unmodifiableList(edges);
    }

    private void checkVertex(int u) {
        if (u < 0 || u >= incidence.size()) {
            throw new IndexOutOfBoundsException(
                    "vertex " + u + " is out of range [0, " + incidence.size() + ")");
        }
    }
}
