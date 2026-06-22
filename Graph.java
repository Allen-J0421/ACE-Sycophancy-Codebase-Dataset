import java.util.List;

/**
 * A graph as the traversal algorithms see it: a fixed set of integer vertices in
 * {@code [0, vertexCount())} and, for each vertex, the edges that can be traversed
 * leaving it.
 *
 * <p>This is the single representation shared by {@link UndirectedGraph} and
 * {@link DirectedGraph}. The only structural difference between them is what
 * {@link #edgesFrom(int)} returns: an undirected edge appears in <em>both</em>
 * endpoints' lists, whereas a directed arc appears only in its tail's list.
 * Algorithms stay representation-agnostic by traversing to {@link Edge#other(int)
 * edge.other(v)} and using {@link Edge#id() edge.id()} when they need edge
 * identity.
 *
 * <p><strong>Which analysis is meaningful on which graph</strong> is a matter of
 * graph theory, not of this API:
 * <ul>
 *   <li>{@link GraphConnectivity} (articulation points and bridges) is meaningful
 *       only on an {@link UndirectedGraph}.</li>
 *   <li>{@link StronglyConnectedComponents} computes SCCs on a
 *       {@link DirectedGraph}; run on an {@link UndirectedGraph} it computes the
 *       connected components, since every undirected edge is symmetric.</li>
 * </ul>
 * Both algorithms accept any {@code Graph}, so either can run on a shared graph
 * object, but the caller is responsible for choosing a combination that means
 * what they intend.
 */
public interface Graph {

    /** Returns the number of vertices in the graph. */
    int vertexCount();

    /**
     * Returns an unmodifiable view of the edges that can be traversed leaving
     * {@code v}. For each returned edge, {@link Edge#other(int) edge.other(v)} is
     * the vertex reached.
     *
     * @param v the source vertex
     * @return the edges leaving {@code v}
     */
    List<Edge> edgesFrom(int v);
}
