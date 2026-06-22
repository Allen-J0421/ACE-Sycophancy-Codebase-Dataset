import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Computes the articulation points (cut vertices) and bridges (cut edges) of an
 * undirected graph using Tarjan's algorithm, expressed as a {@link DfsVisitor}
 * over a shared {@link DepthFirstSearch}. The whole analysis is a single
 * {@code O(V + E)} pass.
 *
 * <p>A vertex is an articulation point if removing it increases the number of
 * connected components; an edge is a bridge if removing it does. Multigraphs are
 * handled correctly: the traversal reports a parallel edge back to the parent as a
 * genuine non-tree edge (only the single arrival edge is suppressed), which
 * matters for bridges, whose strict {@code low[child] > disc[parent]} test a
 * parallel edge can flip.
 *
 * <p>Articulation points and bridges are only meaningful on an
 * {@link UndirectedGraph}. The analysis accepts any {@link Graph}, but on a
 * {@link DirectedGraph} (whose arcs are one-directional) the results are not
 * meaningful.
 *
 * <p>Instances are stateless and therefore reusable and thread-safe: each call to
 * {@link #analyze(Graph)} allocates its own visitor.
 */
public final class GraphConnectivity {

    /** Orders bridges deterministically by smaller endpoint, larger endpoint, then id. */
    private static final Comparator<Edge> BRIDGE_ORDER = Comparator
            .comparingInt((Edge e) -> Math.min(e.u(), e.v()))
            .thenComparingInt(e -> Math.max(e.u(), e.v()))
            .thenComparingInt(Edge::id);

    /**
     * Analyzes the given graph's connectivity.
     *
     * @param graph the graph to analyze
     * @return its articulation points and bridges
     */
    public ConnectivityResult analyze(Graph graph) {
        ConnectivityVisitor visitor = new ConnectivityVisitor(graph.vertexCount());
        new DepthFirstSearch().traverse(graph, visitor);
        return visitor.toResult();
    }

    /**
     * Accumulates articulation points and bridges from the DFS hooks. Low-link
     * values and discovery times live in {@link LowLinkState}; the DFS tree
     * structure (parent, incoming edge, child count) is recorded as the traversal
     * reports it.
     */
    private static final class ConnectivityVisitor implements DfsVisitor {

        private final int vertexCount;
        private final LowLinkState state;
        private final boolean[] isArticulationPoint;
        private final int[] parent;
        private final Edge[] incomingEdge;
        private final int[] childCount;
        private final List<Edge> bridges = new ArrayList<>();

        ConnectivityVisitor(int vertexCount) {
            this.vertexCount = vertexCount;
            this.state = new LowLinkState(vertexCount);
            this.isArticulationPoint = new boolean[vertexCount];
            this.parent = new int[vertexCount];
            this.incomingEdge = new Edge[vertexCount];
            this.childCount = new int[vertexCount];
            Arrays.fill(parent, -1);
        }

        @Override
        public void discoverVertex(int v) {
            state.discover(v);
        }

        @Override
        public void treeEdge(int from, int to, Edge edge) {
            parent[to] = from;
            incomingEdge[to] = edge;
            childCount[from]++;
        }

        @Override
        public void nonTreeEdge(int from, int to, Edge edge) {
            // Ignore the single reverse traversal of the tree edge we arrived on:
            // in an undirected graph it is the same edge, not a back edge. A
            // parallel edge to the parent has a different id and is a real back
            // edge (this is what makes multigraph bridge detection correct).
            Edge treeEdge = incomingEdge[from];
            if (treeEdge != null && edge.id() == treeEdge.id()) {
                return;
            }
            state.relaxAgainstAncestor(from, to);
        }

        @Override
        public void finishVertex(int v) {
            int p = parent[v];
            if (p == -1) {
                // The DFS root is a cut vertex only if it has more than one child.
                if (childCount[v] > 1) {
                    isArticulationPoint[v] = true;
                }
                return;
            }

            state.propagateFromChild(p, v);

            // The incoming tree edge is a bridge when v's subtree has no back edge
            // reaching p or above (strict inequality, so a parallel edge back to p
            // disqualifies it).
            if (state.low(v) > state.discovery(p)) {
                bridges.add(incomingEdge[v]);
            }

            // A non-root vertex p is a cut vertex when one of its children's
            // subtrees cannot reach above it via a back edge.
            boolean parentIsRoot = parent[p] == -1;
            if (!parentIsRoot && state.low(v) >= state.discovery(p)) {
                isArticulationPoint[p] = true;
            }
        }

        ConnectivityResult toResult() {
            List<Integer> articulationPoints = new ArrayList<>();
            for (int v = 0; v < vertexCount; v++) {
                if (isArticulationPoint[v]) {
                    articulationPoints.add(v);
                }
            }
            bridges.sort(BRIDGE_ORDER);
            return new ConnectivityResult(List.copyOf(articulationPoints), List.copyOf(bridges));
        }
    }
}
