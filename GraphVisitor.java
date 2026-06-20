/**
 * A visitor over the elements of a {@link DirectedGraph}, invoked by
 * {@link GraphTraversal}. Implement this to define a custom graph operation —
 * out-degree statistics, reachability, edge counting, and so on — <em>without
 * modifying {@code DirectedGraph}</em>.
 *
 * <p>Both hooks default to no-ops, so a visitor overrides only the events it
 * cares about (e.g. a reachability visitor needs only {@link #visitVertex}).
 * A visitor typically accumulates state during traversal and exposes the result
 * through its own method afterwards.
 */
interface GraphVisitor {

    /** Called once for each vertex the traversal reaches. */
    default void visitVertex(int vertex) {
    }

    /** Called once for each edge {@code from -> to} the traversal scans. */
    default void visitEdge(int from, int to) {
    }
}
