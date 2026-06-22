import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;

/**
 * Computes the strongly connected components (SCCs) of a graph using Tarjan's
 * algorithm, expressed as a {@link DfsVisitor} over a shared
 * {@link DepthFirstSearch}. The whole analysis is a single {@code O(V + E)} pass.
 *
 * <p>Two vertices are in the same SCC if each is reachable from the other. On a
 * {@link DirectedGraph} these are the usual SCCs; on an {@link UndirectedGraph}
 * every edge is symmetric, so the result is the graph's connected components.
 *
 * <p>This shares the DFS engine and {@link LowLinkState} with
 * {@link GraphConnectivity} but is otherwise an independent algorithm, because
 * SCCs and articulation points/bridges are different concepts.
 *
 * <p>Instances are stateless and therefore reusable and thread-safe: each call to
 * {@link #find(Graph)} allocates its own visitor.
 */
public final class StronglyConnectedComponents {

    /**
     * Returns the strongly connected components of the given graph.
     *
     * <p>Each component lists its vertices in ascending order, and the components
     * themselves are ordered by their smallest vertex, so the result is
     * deterministic.
     *
     * @param graph the graph to analyze
     * @return the list of strongly connected components
     */
    public List<List<Integer>> find(Graph graph) {
        SccVisitor visitor = new SccVisitor(graph.vertexCount());
        new DepthFirstSearch().traverse(graph, visitor);
        return visitor.components();
    }

    /**
     * Accumulates strongly connected components from the DFS hooks. A vertex is the
     * root of an SCC when its low-link returns to its own discovery time; at that
     * point the component is popped off the component stack.
     */
    private static final class SccVisitor implements DfsVisitor {

        private final LowLinkState state;
        private final int[] parent;

        /** Whether each vertex is currently on the component stack. */
        private final boolean[] onStack;

        /** Vertices of the SCC currently being assembled, in discovery order. */
        private final Deque<Integer> componentStack = new ArrayDeque<>();

        private final List<List<Integer>> components = new ArrayList<>();

        SccVisitor(int vertexCount) {
            this.state = new LowLinkState(vertexCount);
            this.parent = new int[vertexCount];
            this.onStack = new boolean[vertexCount];
            Arrays.fill(parent, -1);
        }

        @Override
        public void discoverVertex(int v) {
            state.discover(v);
            componentStack.push(v);
            onStack[v] = true;
        }

        @Override
        public void treeEdge(int from, int to, Edge edge) {
            parent[to] = from;
        }

        @Override
        public void nonTreeEdge(int from, int to, Edge edge) {
            // Only relax against vertices still on the stack; an edge into an
            // already-finished SCC must not affect this one.
            if (onStack[to]) {
                state.relaxAgainstAncestor(from, to);
            }
        }

        @Override
        public void finishVertex(int v) {
            int p = parent[v];
            if (p != -1) {
                state.propagateFromChild(p, v);
            }
            if (state.isLowLinkRoot(v)) {
                popComponent(v);
            }
        }

        /** Pops the component stack down to and including its SCC root {@code root}. */
        private void popComponent(int root) {
            List<Integer> component = new ArrayList<>();
            int w;
            do {
                w = componentStack.pop();
                onStack[w] = false;
                component.add(w);
            } while (w != root);
            components.add(component);
        }

        List<List<Integer>> components() {
            for (List<Integer> component : components) {
                Collections.sort(component);
            }
            components.sort(Comparator.comparingInt(component -> component.get(0)));

            List<List<Integer>> result = new ArrayList<>(components.size());
            for (List<Integer> component : components) {
                result.add(List.copyOf(component));
            }
            return List.copyOf(result);
        }
    }
}
