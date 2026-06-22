import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An undirected {@link Graph} backed by an adjacency list. Each edge carries a
 * stable {@link Edge#id() identity} and is shared between its two endpoints'
 * incidence lists, so parallel edges are distinguishable — which bridge detection
 * requires. Parallel edges and self-loops are permitted.
 */
public final class UndirectedGraph implements Graph {

    /** For each vertex, the edges incident to it (each undirected edge appears twice). */
    private final List<List<Edge>> incidence;

    /** All edges, in insertion order; an edge's index equals its {@link Edge#id()}. */
    private final List<Edge> edges = new ArrayList<>();

    /**
     * Creates a graph with the given number of vertices and no edges.
     *
     * @param vertexCount the number of vertices; must be non-negative
     */
    public UndirectedGraph(int vertexCount) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("vertexCount must be non-negative: " + vertexCount);
        }
        incidence = new ArrayList<>(vertexCount);
        for (int i = 0; i < vertexCount; i++) {
            incidence.add(new ArrayList<>());
        }
    }

    /**
     * Builds an undirected graph from a list of edges.
     *
     * @param vertexCount the number of vertices
     * @param edges       an array of {@code {u, v}} pairs
     * @return the constructed graph
     */
    public static UndirectedGraph fromEdges(int vertexCount, int[][] edges) {
        UndirectedGraph graph = new UndirectedGraph(vertexCount);
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

    @Override
    public int vertexCount() {
        return incidence.size();
    }

    /** Returns the number of edges in the graph. */
    public int edgeCount() {
        return edges.size();
    }

    @Override
    public List<Edge> edgesFrom(int v) {
        checkVertex(v);
        return Collections.unmodifiableList(incidence.get(v));
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
