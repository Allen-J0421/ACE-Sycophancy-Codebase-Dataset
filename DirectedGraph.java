import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A directed graph backed by an adjacency list of out-neighbors.
 *
 * <p>Vertices are identified by integers in the range {@code [0, vertexCount())}.
 * Parallel arcs and self-loops are permitted; they do not affect strongly
 * connected components, so this representation tracks targets rather than edge
 * identities (unlike the undirected {@link Graph}, whose bridge detection needs
 * them).
 */
public final class DirectedGraph {

    /** For each vertex, the vertices it has an arc to. */
    private final List<List<Integer>> outAdjacency;

    /**
     * Creates a directed graph with the given number of vertices and no arcs.
     *
     * @param vertexCount the number of vertices; must be non-negative
     */
    public DirectedGraph(int vertexCount) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("vertexCount must be non-negative: " + vertexCount);
        }
        outAdjacency = new ArrayList<>(vertexCount);
        for (int i = 0; i < vertexCount; i++) {
            outAdjacency.add(new ArrayList<>());
        }
    }

    /**
     * Builds a directed graph from a list of arcs.
     *
     * @param vertexCount the number of vertices
     * @param arcs        an array of {@code {from, to}} pairs
     * @return the constructed graph
     */
    public static DirectedGraph fromEdges(int vertexCount, int[][] arcs) {
        DirectedGraph graph = new DirectedGraph(vertexCount);
        for (int[] arc : arcs) {
            if (arc.length != 2) {
                throw new IllegalArgumentException(
                        "each arc must have exactly two endpoints, found " + arc.length);
            }
            graph.addEdge(arc[0], arc[1]);
        }
        return graph;
    }

    /**
     * Adds a directed arc {@code from -> to}.
     *
     * @param from the tail (source) vertex
     * @param to   the head (target) vertex
     */
    public void addEdge(int from, int to) {
        checkVertex(from);
        checkVertex(to);
        outAdjacency.get(from).add(to);
    }

    /** Returns the number of vertices in the graph. */
    public int vertexCount() {
        return outAdjacency.size();
    }

    /**
     * Returns an unmodifiable view of the vertices reachable from {@code u} by a
     * single arc.
     *
     * @param u the source vertex
     * @return the out-neighbors of {@code u}
     */
    public List<Integer> outNeighbors(int u) {
        checkVertex(u);
        return Collections.unmodifiableList(outAdjacency.get(u));
    }

    private void checkVertex(int u) {
        if (u < 0 || u >= outAdjacency.size()) {
            throw new IndexOutOfBoundsException(
                    "vertex " + u + " is out of range [0, " + outAdjacency.size() + ")");
        }
    }
}
