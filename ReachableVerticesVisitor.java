import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A {@link GraphVisitor} that collects the set of vertices visited during a
 * traversal. Paired with {@link GraphTraversal#traverseFrom}, it yields the
 * vertices reachable from a source (the source included); paired with
 * {@link GraphTraversal#visitAll}, it yields every vertex.
 */
final class ReachableVerticesVisitor implements GraphVisitor {

    private final Set<Integer> visited = new LinkedHashSet<>();

    @Override
    public void visitVertex(int vertex) {
        visited.add(vertex);
    }

    /** Returns the visited vertices, in the order they were first reached. */
    Set<Integer> reachable() {
        return Collections.unmodifiableSet(visited);
    }
}
