package bipartite;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Performs a depth-first traversal of any {@link Graph}, visiting each reachable
 * vertex exactly once and reporting the order in which vertices are first
 * discovered (preorder).
 *
 * <p>The search is iterative — it uses an explicit stack rather than recursion —
 * so it handles deep graphs without risking a stack overflow, mirroring the
 * iterative breadth-first search in {@link BipartiteChecker}. It depends only on
 * the {@link Graph} interface, so it runs unchanged on every implementation.
 * Each traversal runs in {@code O(V + E)} time and {@code O(V)} additional space.
 */
public final class DepthFirstSearch {

    /**
     * Traverses the whole graph, visiting every vertex. Disconnected components
     * are covered by restarting the search from the next undiscovered vertex in
     * ascending id order.
     *
     * @param graph the graph to traverse (any {@link Graph} implementation)
     * @return the vertices in depth-first preorder
     * @throws NullPointerException if {@code graph} is null
     */
    public List<Integer> traverse(Graph graph) {
        if (graph == null) {
            throw new NullPointerException("graph must not be null");
        }

        boolean[] discovered = new boolean[graph.order()];
        List<Integer> order = new ArrayList<>(graph.order());
        for (int start = 0; start < graph.order(); start++) {
            if (!discovered[start]) {
                explore(graph, start, discovered, order);
            }
        }
        return order;
    }

    /**
     * Traverses only the vertices reachable from {@code source}.
     *
     * @param graph  the graph to traverse (any {@link Graph} implementation)
     * @param source the vertex to start from, in {@code [0, graph.order())}
     * @return the reachable vertices in depth-first preorder, beginning with
     *         {@code source}
     * @throws NullPointerException      if {@code graph} is null
     * @throws IndexOutOfBoundsException if {@code source} is out of range
     */
    public List<Integer> traverseFrom(Graph graph, int source) {
        if (graph == null) {
            throw new NullPointerException("graph must not be null");
        }
        if (source < 0 || source >= graph.order()) {
            throw new IndexOutOfBoundsException(
                    "Vertex " + source + " is outside the range [0, " + graph.order() + ")");
        }

        boolean[] discovered = new boolean[graph.order()];
        List<Integer> order = new ArrayList<>();
        explore(graph, source, discovered, order);
        return order;
    }

    /**
     * Iteratively explores the component reachable from {@code start}, appending
     * each vertex to {@code order} when it is first discovered. Neighbors are
     * pushed in reverse so the lowest-id neighbor is processed first, producing
     * the same preorder a recursive depth-first search would.
     */
    private static void explore(Graph graph, int start, boolean[] discovered, List<Integer> order) {
        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(start);

        while (!stack.isEmpty()) {
            int u = stack.pop();
            if (discovered[u]) {
                // A vertex can be stacked more than once before it is reached;
                // skip the redundant visits.
                continue;
            }
            discovered[u] = true;
            order.add(u);

            List<Integer> neighbors = graph.neighbors(u);
            for (int i = neighbors.size() - 1; i >= 0; i--) {
                int neighbor = neighbors.get(i);
                if (!discovered[neighbor]) {
                    stack.push(neighbor);
                }
            }
        }
    }
}
