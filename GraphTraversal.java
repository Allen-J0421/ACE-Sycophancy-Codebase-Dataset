import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Traversal engine that walks a {@link DirectedGraph} and dispatches its
 * vertices and edges to a {@link GraphVisitor}. It reads only the graph's public
 * API, so new operations are written as visitors here — the {@code DirectedGraph}
 * class itself never changes.
 */
final class GraphTraversal {

    private GraphTraversal() {
    }

    /**
     * Visits every vertex (in ascending order) and every edge of {@code graph}
     * exactly once. Suitable for whole-graph operations such as degree statistics.
     */
    static void visitAll(DirectedGraph graph, GraphVisitor visitor) {
        int vertexCount = graph.vertices();
        for (int u = 0; u < vertexCount; u++) {
            visitor.visitVertex(u);
            for (int v : graph.neighbors(u)) {
                visitor.visitEdge(u, v);
            }
        }
    }

    /**
     * Breadth-first traversal of every vertex reachable from {@code source}
     * (including {@code source} itself). Each reachable vertex triggers
     * {@link GraphVisitor#visitVertex} once; every scanned edge triggers
     * {@link GraphVisitor#visitEdge}.
     *
     * @throws IndexOutOfBoundsException if {@code source} is not a valid vertex
     */
    static void traverseFrom(DirectedGraph graph, int source, GraphVisitor visitor) {
        int vertexCount = graph.vertices();
        if (source < 0 || source >= vertexCount) {
            throw new IndexOutOfBoundsException(
                    "source " + source + " out of range [0, " + vertexCount + ")");
        }

        boolean[] seen = new boolean[vertexCount];
        Deque<Integer> queue = new ArrayDeque<>();
        seen[source] = true;
        queue.add(source);

        while (!queue.isEmpty()) {
            int u = queue.poll();
            visitor.visitVertex(u);
            for (int v : graph.neighbors(u)) {
                visitor.visitEdge(u, v);
                if (!seen[v]) {
                    seen[v] = true;
                    queue.add(v);
                }
            }
        }
    }
}
