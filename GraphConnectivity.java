import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;

/**
 * Computes the articulation points (cut vertices) and bridges (cut edges) of an
 * undirected graph using Tarjan's depth-first search algorithm. Both are derived
 * from the same low-link traversal in a single {@code O(V + E)} pass.
 *
 * <p>A vertex is an articulation point if removing it increases the number of
 * connected components; an edge is a bridge if removing it does. Multigraphs are
 * handled correctly: the traversal skips only the specific tree edge it arrived
 * on (by {@link Graph.Edge#id() edge id}), so a parallel edge back to the parent
 * is treated as a genuine back edge. This matters for bridges, whose strict
 * {@code low[child] > disc[parent]} test a parallel edge can flip.
 *
 * <p>Instances are stateless and therefore reusable and thread-safe: each call to
 * {@link #analyze(Graph)} allocates its own traversal state.
 *
 * <p>The traversal is implemented with an explicit stack rather than recursion, so
 * it handles arbitrarily deep graphs without risking a {@link StackOverflowError}.
 */
public final class GraphConnectivity {

    /** Orders bridges deterministically by smaller endpoint, larger endpoint, then id. */
    private static final Comparator<Graph.Edge> BRIDGE_ORDER = Comparator
            .comparingInt((Graph.Edge e) -> Math.min(e.u(), e.v()))
            .thenComparingInt(e -> Math.max(e.u(), e.v()))
            .thenComparingInt(Graph.Edge::id);

    /**
     * Analyzes the given graph's connectivity.
     *
     * @param graph the graph to analyze
     * @return its articulation points and bridges
     */
    public ConnectivityResult analyze(Graph graph) {
        return new Search(graph).run();
    }

    /**
     * Holds the mutable depth-first-search state for a single
     * {@link #analyze(Graph)} invocation, keeping {@link GraphConnectivity}
     * itself stateless.
     */
    private static final class Search {

        private final Graph graph;
        private final LowLinkState state;
        private final boolean[] isArticulationPoint;
        private final List<Graph.Edge> bridges = new ArrayList<>();

        Search(Graph graph) {
            int vertexCount = graph.vertexCount();
            this.graph = graph;
            this.state = new LowLinkState(vertexCount);
            this.isArticulationPoint = new boolean[vertexCount];
        }

        ConnectivityResult run() {
            int vertexCount = graph.vertexCount();
            for (int u = 0; u < vertexCount; u++) {
                if (!state.isDiscovered(u)) {
                    explore(u);
                }
            }

            List<Integer> articulationPoints = new ArrayList<>();
            for (int u = 0; u < vertexCount; u++) {
                if (isArticulationPoint[u]) {
                    articulationPoints.add(u);
                }
            }
            bridges.sort(BRIDGE_ORDER);
            return new ConnectivityResult(
                    List.copyOf(articulationPoints), List.copyOf(bridges));
        }

        /**
         * Explores the DFS tree rooted at {@code root} using an explicit stack.
         *
         * <p>The stack always holds the current root-to-node path, which lets each
         * vertex fold its low-link, cut-vertex verdict, and the bridge status of its
         * incoming edge into its parent the moment it is fully explored — the
         * iterative equivalent of returning from a recursive call.
         */
        private void explore(int root) {
            state.discover(root);

            Deque<Frame> stack = new ArrayDeque<>();
            stack.push(new Frame(root, null));

            while (!stack.isEmpty()) {
                Frame frame = stack.peek();
                int u = frame.vertex;
                List<Graph.Edge> incident = graph.incidentEdges(u);

                if (frame.nextEdge < incident.size()) {
                    Graph.Edge edge = incident.get(frame.nextEdge++);
                    int v = edge.other(u);
                    if (v == u) {
                        // Self-loop: irrelevant to articulation points and bridges.
                        continue;
                    }
                    if (!state.isDiscovered(v)) {
                        frame.children++;
                        state.discover(v);
                        stack.push(new Frame(v, edge));
                    } else if (!edge.equals(frame.parentEdge)) {
                        // Back edge — but skip only the single tree edge we arrived on.
                        // A parallel edge to the parent has a different id, so it counts.
                        state.relaxAgainstAncestor(u, v);
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
         * propagates its low-link upward, may mark its parent as a cut vertex, and may
         * mark its incoming tree edge as a bridge.
         *
         * @param u           the vertex that has just finished exploring
         * @param frame       {@code u}'s traversal frame
         * @param parentFrame the frame of {@code u}'s parent, or {@code null} if
         *                    {@code u} is the DFS root
         */
        private void foldIntoParent(int u, Frame frame, Frame parentFrame) {
            Graph.Edge incoming = frame.parentEdge;
            if (incoming == null) {
                // The DFS root is a cut vertex only if it has more than one DFS child.
                if (frame.children > 1) {
                    isArticulationPoint[u] = true;
                }
                return;
            }

            int parent = incoming.other(u);
            state.propagateFromChild(parent, u);

            // The incoming tree edge is a bridge when u's subtree has no back edge
            // reaching the parent or above (strict inequality, so a parallel edge
            // back to the parent disqualifies it).
            if (state.low(u) > state.discovery(parent)) {
                bridges.add(incoming);
            }

            // A non-root vertex is a cut vertex when one of its children's subtrees
            // cannot reach above it via a back edge.
            boolean parentIsRoot = parentFrame.parentEdge == null;
            if (!parentIsRoot && state.low(u) >= state.discovery(parent)) {
                isArticulationPoint[parent] = true;
            }
        }
    }

    /** One vertex's state on the explicit DFS stack. */
    private static final class Frame {

        final int vertex;

        /** The tree edge by which {@link #vertex} was reached, or {@code null} for the root. */
        final Graph.Edge parentEdge;

        /** Index of the next incident edge to visit from {@link #vertex}. */
        int nextEdge;

        /** Number of DFS-tree children discovered from {@link #vertex}. */
        int children;

        Frame(int vertex, Graph.Edge parentEdge) {
            this.vertex = vertex;
            this.parentEdge = parentEdge;
        }
    }
}
