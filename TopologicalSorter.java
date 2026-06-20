import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Optional;

/**
 * Produces a topological ordering of a {@link DirectedGraph} using Kahn's
 * algorithm: repeatedly emit a vertex whose in-degree has dropped to zero. The
 * traversal is iterative (queue-driven), so it handles arbitrarily large graphs
 * without recursion, and it decides the DAG question and the ordering in a single
 * pass — a graph has a topological ordering exactly when it is acyclic.
 *
 * <p>Runs in O(V + E) time and O(V) extra space. This is the constructive
 * counterpart to {@link KahnCycleDetector}, which answers the same acyclicity
 * question without materialising an ordering.
 */
final class TopologicalSorter {

    /**
     * Returns a valid topological ordering of {@code graph} if it is a DAG, or
     * {@link Optional#empty()} if the graph contains a cycle (and therefore has
     * no linear ordering). For every edge {@code u -> v}, {@code u} precedes
     * {@code v} in the returned order. The list is unmodifiable.
     */
    Optional<List<Integer>> sort(DirectedGraph graph) {
        int vertexCount = graph.vertices();

        int[] inDegree = new int[vertexCount];
        for (int u = 0; u < vertexCount; u++) {
            for (int v : graph.neighbors(u)) {
                inDegree[v]++;
            }
        }

        Deque<Integer> ready = new ArrayDeque<>();
        for (int v = 0; v < vertexCount; v++) {
            if (inDegree[v] == 0) {
                ready.add(v);
            }
        }

        List<Integer> order = new ArrayList<>(vertexCount);
        while (!ready.isEmpty()) {
            int u = ready.poll();
            order.add(u);
            for (int v : graph.neighbors(u)) {
                if (--inDegree[v] == 0) {
                    ready.add(v);
                }
            }
        }

        // Fewer vertices ordered than the graph holds => the remainder form a cycle.
        if (order.size() != vertexCount) {
            return Optional.empty();
        }
        return Optional.of(Collections.unmodifiableList(order));
    }

    /** Returns whether {@code graph} is a DAG, i.e. it has a topological ordering. */
    boolean isDag(DirectedGraph graph) {
        return sort(graph).isPresent();
    }
}
