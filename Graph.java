import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An undirected graph backed by an adjacency list.
 *
 * <p>Vertices are identified by integers in the range {@code [0, vertexCount())}.
 */
public final class Graph {

    private final List<List<Integer>> adjacency;

    /**
     * Creates a graph with the given number of vertices and no edges.
     *
     * @param vertexCount the number of vertices; must be non-negative
     */
    public Graph(int vertexCount) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("vertexCount must be non-negative: " + vertexCount);
        }
        adjacency = new ArrayList<>(vertexCount);
        for (int i = 0; i < vertexCount; i++) {
            adjacency.add(new ArrayList<>());
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
     * Adds an undirected edge between two vertices.
     *
     * @param u one endpoint
     * @param v the other endpoint
     */
    public void addEdge(int u, int v) {
        checkVertex(u);
        checkVertex(v);
        adjacency.get(u).add(v);
        adjacency.get(v).add(u);
    }

    /** Returns the number of vertices in the graph. */
    public int vertexCount() {
        return adjacency.size();
    }

    /**
     * Returns an unmodifiable view of the vertices adjacent to {@code u}.
     *
     * @param u the vertex whose neighbors are requested
     * @return the neighbors of {@code u}
     */
    public List<Integer> neighbors(int u) {
        checkVertex(u);
        return Collections.unmodifiableList(adjacency.get(u));
    }

    private void checkVertex(int u) {
        if (u < 0 || u >= adjacency.size()) {
            throw new IndexOutOfBoundsException(
                    "vertex " + u + " is out of range [0, " + adjacency.size() + ")");
        }
    }
}
