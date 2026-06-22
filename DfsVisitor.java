/**
 * Receives callbacks during a depth-first traversal driven by
 * {@link DepthFirstSearch}. Implementations override only the hooks they need;
 * every hook defaults to a no-op.
 *
 * <p>The hooks, for each vertex {@code v} reached from a start vertex:
 * <ul>
 *   <li>{@link #discoverVertex(int)} — the first time {@code v} is reached
 *       (pre-order). For a non-root vertex it fires immediately after the
 *       {@link #treeEdge(int, int, Edge) treeEdge} that reaches it.</li>
 *   <li>{@link #treeEdge(int, int, Edge)} — when the search follows an edge from
 *       an explored vertex to a not-yet-discovered one.</li>
 *   <li>{@link #nonTreeEdge(int, int, Edge)} — when an edge leads to an
 *       already-discovered vertex and is <em>not</em> the edge the search arrived
 *       on. (On an undirected graph the single reverse traversal of the tree edge
 *       is suppressed by edge identity, but parallel edges and self-loops are
 *       reported; on a directed graph every non-tree arc is reported.)</li>
 *   <li>{@link #finishVertex(int)} — when {@code v}'s outgoing edges are
 *       exhausted (post-order).</li>
 * </ul>
 */
public interface DfsVisitor {

    /** Called the first time {@code v} is reached. */
    default void discoverVertex(int v) {
    }

    /** Called when {@code edge} is followed from {@code from} to undiscovered {@code to}. */
    default void treeEdge(int from, int to, Edge edge) {
    }

    /**
     * Called when {@code edge} leads from {@code from} to already-discovered
     * {@code to} and is not the edge the search arrived on at {@code from}.
     */
    default void nonTreeEdge(int from, int to, Edge edge) {
    }

    /** Called when {@code v}'s outgoing edges are exhausted (post-order). */
    default void finishVertex(int v) {
    }
}
