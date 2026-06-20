package bipartite;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Produces a topological ordering of a {@link DirectedGraph}: a sequence of all
 * vertices in which every edge {@code u -> v} has {@code u} before {@code v}, so
 * dependencies precede the vertices that depend on them. Such an order exists if
 * and only if the graph is acyclic.
 *
 * <p>The ordering is computed with Kahn's algorithm — repeatedly emitting a
 * vertex whose remaining in-degree is zero — which is iterative and BFS-flavoured
 * like the search in {@link BipartiteChecker}. Ties are broken by ascending
 * vertex id (via a min-heap), so the result is the unique lexicographically
 * smallest topological order. If a cycle prevents a complete ordering, a directed
 * cycle is reconstructed as a verifiable witness, mirroring the odd-cycle witness
 * of {@link BipartiteChecker}. The sort runs in {@code O((V + E) log V)} time
 * (the log factor coming from the heap) and {@code O(V + E)} space.
 *
 * <p>Topological order is a property of directed graphs, so this takes a
 * {@link DirectedGraph}; it relies only on the {@link Graph} interface internally.
 */
public final class TopologicalSort {

    private static final int UNVISITED = 0;
    private static final int IN_PROGRESS = 1;
    private static final int DONE = 2;

    /**
     * Topologically sorts the given graph.
     *
     * @param graph the directed graph to sort
     * @return a {@link TopologicalSortResult.Sorted} with the ordering if the
     *         graph is acyclic, otherwise a {@link TopologicalSortResult.Cyclic}
     *         carrying a directed-cycle witness
     * @throws NullPointerException if {@code graph} is null
     */
    public TopologicalSortResult sort(DirectedGraph graph) {
        if (graph == null) {
            throw new NullPointerException("graph must not be null");
        }

        int order = graph.order();
        int[] inDegree = inDegrees(graph);

        // Seed the queue with every source (in-degree zero); the min-heap makes
        // ties resolve to the smallest vertex id for a deterministic result.
        PriorityQueue<Integer> ready = new PriorityQueue<>();
        for (int vertex = 0; vertex < order; vertex++) {
            if (inDegree[vertex] == 0) {
                ready.add(vertex);
            }
        }

        List<Integer> sorted = new ArrayList<>(order);
        while (!ready.isEmpty()) {
            int u = ready.poll();
            sorted.add(u);
            for (int v : graph.neighbors(u)) {
                if (--inDegree[v] == 0) {
                    ready.add(v);
                }
            }
        }

        if (sorted.size() == order) {
            return new TopologicalSortResult.Sorted(sorted);
        }
        // Vertices never reaching in-degree zero form the cyclic remainder.
        return new TopologicalSortResult.Cyclic(findCycle(graph, inDegree));
    }

    private static int[] inDegrees(DirectedGraph graph) {
        int[] inDegree = new int[graph.order()];
        for (int u = 0; u < graph.order(); u++) {
            for (int v : graph.neighbors(u)) {
                inDegree[v]++;
            }
        }
        return inDegree;
    }

    /**
     * Finds a directed cycle within the vertices that Kahn's algorithm could not
     * order — those still carrying a positive in-degree. Every such vertex has an
     * incoming edge from another such vertex, so the induced subgraph is
     * guaranteed to contain a cycle; a depth-first search over it locates a back
     * edge to a vertex on the current path.
     */
    private static List<Integer> findCycle(DirectedGraph graph, int[] inDegree) {
        int order = graph.order();
        int[] state = new int[order];
        // Vertices that were successfully ordered are not part of any cycle.
        for (int vertex = 0; vertex < order; vertex++) {
            if (inDegree[vertex] == 0) {
                state[vertex] = DONE;
            }
        }

        for (int start = 0; start < order; start++) {
            if (state[start] == UNVISITED) {
                List<Integer> cycle = depthFirstCycle(graph, start, state);
                if (cycle != null) {
                    return cycle;
                }
            }
        }
        throw new IllegalStateException("a cycle was expected but none was found");
    }

    /**
     * Iterative depth-first search from {@code start} that returns the first
     * directed cycle it encounters (an edge to a vertex still on the current
     * path), or {@code null} if this component has none.
     */
    private static List<Integer> depthFirstCycle(DirectedGraph graph, int start, int[] state) {
        Deque<int[]> stack = new ArrayDeque<>();   // frames of {vertex, nextNeighborIndex}
        Deque<Integer> path = new ArrayDeque<>();  // vertices currently in progress, deepest first
        state[start] = IN_PROGRESS;
        stack.push(new int[] {start, 0});
        path.push(start);

        while (!stack.isEmpty()) {
            int[] frame = stack.peek();
            int u = frame[0];
            List<Integer> neighbors = graph.neighbors(u);

            if (frame[1] < neighbors.size()) {
                int v = neighbors.get(frame[1]++);
                if (state[v] == DONE) {
                    continue;
                }
                if (state[v] == IN_PROGRESS) {
                    return extractCycle(path, v);
                }
                state[v] = IN_PROGRESS;
                stack.push(new int[] {v, 0});
                path.push(v);
            } else {
                state[u] = DONE;
                stack.pop();
                path.pop();
            }
        }
        return null;
    }

    /**
     * Extracts the cycle closed by a back edge to {@code target}: the suffix of
     * the current path from {@code target} down to the deepest vertex, returned
     * in edge order so that consecutive entries (and the closing entry back to
     * the first) are directed edges.
     */
    private static List<Integer> extractCycle(Deque<Integer> path, int target) {
        List<Integer> cycle = new ArrayList<>();
        for (int vertex : path) {  // deepest vertex first, down to the path root
            cycle.add(vertex);
            if (vertex == target) {
                break;
            }
        }
        Collections.reverse(cycle);
        return cycle;
    }
}
