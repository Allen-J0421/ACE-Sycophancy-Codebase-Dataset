import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;

/**
 * Computes the strongly connected components (SCCs) of a graph using Tarjan's
 * algorithm in a single {@code O(V + E)} pass.
 *
 * <p>Two vertices are in the same SCC if each is reachable from the other. On a
 * {@link DirectedGraph} these are the usual SCCs; on an {@link UndirectedGraph}
 * every edge is symmetric, so the result is the graph's connected components.
 *
 * <p>This shares the low-link DFS technique with {@link GraphConnectivity} (via
 * {@link LowLinkState}) but is otherwise an independent algorithm, because SCCs
 * and articulation points/bridges are different concepts.
 *
 * <p>Instances are stateless and therefore reusable and thread-safe: each call to
 * {@link #find(Graph)} allocates its own traversal state. The traversal is
 * iterative, so it handles arbitrarily deep graphs without risking a
 * {@link StackOverflowError}.
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
        return new Search(graph).run();
    }

    /**
     * Holds the mutable Tarjan-SCC state for a single {@link #find(Graph)}
     * invocation, keeping {@link StronglyConnectedComponents} itself stateless.
     */
    private static final class Search {

        private final Graph graph;
        private final LowLinkState state;

        /** Whether each vertex is currently on the component stack. */
        private final boolean[] onStack;

        /** Vertices of the SCC currently being assembled, in discovery order. */
        private final Deque<Integer> componentStack = new ArrayDeque<>();

        private final List<List<Integer>> components = new ArrayList<>();

        Search(Graph graph) {
            int vertexCount = graph.vertexCount();
            this.graph = graph;
            this.state = new LowLinkState(vertexCount);
            this.onStack = new boolean[vertexCount];
        }

        List<List<Integer>> run() {
            int vertexCount = graph.vertexCount();
            for (int v = 0; v < vertexCount; v++) {
                if (!state.isDiscovered(v)) {
                    explore(v);
                }
            }

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

        /**
         * Explores the DFS tree rooted at {@code root} with an explicit stack. When a
         * vertex's low-link equals its own discovery index, it is the root of an SCC,
         * and that component is popped off the component stack.
         */
        private void explore(int root) {
            Deque<Frame> work = new ArrayDeque<>();
            discover(root);
            work.push(new Frame(root));

            while (!work.isEmpty()) {
                Frame frame = work.peek();
                int v = frame.vertex;
                List<Edge> outEdges = graph.edgesFrom(v);

                if (frame.nextArc < outEdges.size()) {
                    int w = outEdges.get(frame.nextArc++).other(v);
                    if (!state.isDiscovered(w)) {
                        discover(w);
                        work.push(new Frame(w));
                    } else if (onStack[w]) {
                        // Back or cross arc into a vertex still on the stack.
                        state.relaxAgainstAncestor(v, w);
                    }
                } else {
                    // v is fully explored; propagate its low-link to its parent.
                    work.pop();
                    Frame parentFrame = work.peek();
                    if (parentFrame != null) {
                        state.propagateFromChild(parentFrame.vertex, v);
                    }
                    if (state.isLowLinkRoot(v)) {
                        popComponent(v);
                    }
                }
            }
        }

        private void discover(int v) {
            state.discover(v);
            componentStack.push(v);
            onStack[v] = true;
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
    }

    /** One vertex's state on the explicit DFS stack. */
    private static final class Frame {

        final int vertex;

        /** Index of the next out-arc to follow from {@link #vertex}. */
        int nextArc;

        Frame(int vertex) {
            this.vertex = vertex;
        }
    }
}
