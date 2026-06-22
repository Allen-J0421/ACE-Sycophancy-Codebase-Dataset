import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A directed {@link Graph} backed by an adjacency list of out-arcs. Each arc is
 * stored only in its tail's list, so {@link #edgesFrom(int)} returns a vertex's
 * out-arcs. Parallel arcs and self-loops are permitted.
 *
 * <p>Arcs carry an {@link Edge#id() identity} for uniformity with the shared
 * {@link Graph} interface, though strongly-connected-components analysis does not
 * rely on it.
 */
public final class DirectedGraph implements Graph {

    /** For each vertex, the arcs whose tail is that vertex. */
    private final List<List<Edge>> outArcs;

    /** All arcs, in insertion order; an arc's index equals its {@link Edge#id()}. */
    private final List<Edge> arcs = new ArrayList<>();

    /**
     * Creates a directed graph with the given number of vertices and no arcs.
     *
     * @param vertexCount the number of vertices; must be non-negative
     */
    public DirectedGraph(int vertexCount) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("vertexCount must be non-negative: " + vertexCount);
        }
        outArcs = new ArrayList<>(vertexCount);
        for (int i = 0; i < vertexCount; i++) {
            outArcs.add(new ArrayList<>());
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
     * @return the newly created arc
     */
    public Edge addEdge(int from, int to) {
        checkVertex(from);
        checkVertex(to);
        Edge arc = new Edge(from, to, arcs.size());
        arcs.add(arc);
        outArcs.get(from).add(arc);
        return arc;
    }

    @Override
    public int vertexCount() {
        return outArcs.size();
    }

    /** Returns the number of arcs in the graph. */
    public int arcCount() {
        return arcs.size();
    }

    @Override
    public List<Edge> edgesFrom(int v) {
        checkVertex(v);
        return Collections.unmodifiableList(outArcs.get(v));
    }

    /** Returns an unmodifiable view of all arcs in the graph. */
    public List<Edge> arcs() {
        return Collections.unmodifiableList(arcs);
    }

    private void checkVertex(int u) {
        if (u < 0 || u >= outArcs.size()) {
            throw new IndexOutOfBoundsException(
                    "vertex " + u + " is out of range [0, " + outArcs.size() + ")");
        }
    }
}
