import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Finds the articulation points (cut vertices) of an undirected graph using
 * Tarjan's depth-first search algorithm.
 *
 * <p>A vertex is an articulation point if removing it (and its incident edges)
 * increases the number of connected components of the graph. The algorithm runs
 * in {@code O(V + E)} time.
 *
 * <p>Instances are stateless and therefore reusable and thread-safe: each call to
 * {@link #find(Graph)} allocates its own traversal state.
 *
 * <p>The traversal is implemented with an explicit stack rather than recursion, so
 * it handles arbitrarily deep graphs without risking a {@link StackOverflowError}.
 */
public final class ArticulationPointFinder {

    /**
     * Returns the articulation points of the given graph in ascending order.
     *
     * @param graph the graph to analyze
     * @return the sorted list of articulation-point vertices (empty if none)
     */
    public List<Integer> find(Graph graph) {
        return new Search(graph).run();
    }

    /**
     * Holds the mutable depth-first-search state for a single {@link #find(Graph)}
     * invocation, keeping {@link ArticulationPointFinder} itself stateless.
     */
    private static final class Search {

        private final Graph graph;

        /** Discovery time of each vertex in the DFS traversal. */
        private final int[] discovery;

        /** Lowest discovery time reachable from each vertex's DFS subtree. */
        private final int[] low;

        private final boolean[] visited;
        private final boolean[] isArticulationPoint;

        /** Monotonically increasing DFS timestamp counter. */
        private int timer;

        Search(Graph graph) {
            int vertexCount = graph.vertexCount();
            this.graph = graph;
            this.discovery = new int[vertexCount];
            this.low = new int[vertexCount];
            this.visited = new boolean[vertexCount];
            this.isArticulationPoint = new boolean[vertexCount];
        }

        List<Integer> run() {
            int vertexCount = graph.vertexCount();
            for (int u = 0; u < vertexCount; u++) {
                if (!visited[u]) {
                    explore(u);
                }
            }

            List<Integer> result = new ArrayList<>();
            for (int u = 0; u < vertexCount; u++) {
                if (isArticulationPoint[u]) {
                    result.add(u);
                }
            }
            return result;
        }

        /**
         * Explores the DFS tree rooted at {@code root} using an explicit stack.
         *
         * <p>The stack always holds the current root-to-node path, which lets each
         * vertex fold its low-link and cut-vertex verdict into its parent the moment
         * it is fully explored — the iterative equivalent of returning from a
         * recursive call.
         */
        private void explore(int root) {
            visited[root] = true;
            discovery[root] = low[root] = ++timer;

            Deque<Frame> stack = new ArrayDeque<>();
            stack.push(new Frame(root, -1));

            while (!stack.isEmpty()) {
                Frame frame = stack.peek();
                int u = frame.vertex;
                List<Integer> neighbors = graph.neighbors(u);

                if (frame.nextNeighbor < neighbors.size()) {
                    int v = neighbors.get(frame.nextNeighbor++);
                    if (!visited[v]) {
                        frame.children++;
                        visited[v] = true;
                        discovery[v] = low[v] = ++timer;
                        stack.push(new Frame(v, u));
                    } else if (v != frame.parent) {
                        // Back edge: update the low-link, but ignore the edge to the parent.
                        low[u] = Math.min(low[u], discovery[v]);
                    }
                } else {
                    // u is fully explored; fold its result into its parent.
                    stack.pop();
                    foldIntoParent(u, frame, stack.peek());
                }
            }
        }

        /**
         * Applies the post-order step for a fully explored vertex {@code u}: the DFS
         * root becomes a cut vertex with more than one child, while any other vertex
         * propagates its low-link upward and may mark its parent as a cut vertex.
         *
         * @param u           the vertex that has just finished exploring
         * @param frame       {@code u}'s traversal frame
         * @param parentFrame the frame of {@code u}'s parent, or {@code null} if
         *                    {@code u} is the DFS root
         */
        private void foldIntoParent(int u, Frame frame, Frame parentFrame) {
            if (frame.parent == -1) {
                // The DFS root is a cut vertex only if it has more than one DFS child.
                if (frame.children > 1) {
                    isArticulationPoint[u] = true;
                }
                return;
            }

            int parent = frame.parent;
            low[parent] = Math.min(low[parent], low[u]);

            // A non-root vertex is a cut vertex when one of its children's subtrees
            // cannot reach above it via a back edge.
            boolean parentIsRoot = parentFrame.parent == -1;
            if (!parentIsRoot && low[u] >= discovery[parent]) {
                isArticulationPoint[parent] = true;
            }
        }
    }

    /** One vertex's state on the explicit DFS stack. */
    private static final class Frame {

        final int vertex;
        final int parent;

        /** Index of the next neighbor to visit from {@link #vertex}. */
        int nextNeighbor;

        /** Number of DFS-tree children discovered from {@link #vertex}. */
        int children;

        Frame(int vertex, int parent) {
            this.vertex = vertex;
            this.parent = parent;
        }
    }
}
