import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Prim's algorithm for computing a minimum spanning tree on a dense graph,
 * using the classic O(V^2) adjacency-matrix formulation.
 *
 * <p>The tree is grown from vertex {@code 0}. On each step the not-yet-included
 * vertex reachable by the cheapest edge is added, and the candidate edge costs
 * of its neighbours are relaxed.
 */
public final class PrimsMST implements MstAlgorithm {

    /** Per-vertex bookkeeping used while growing the spanning tree. */
    private static final class VertexState {
        final int[] parent;       // cheapest known neighbour already in the tree
        final int[] key;          // weight of the edge to {@code parent}
        final boolean[] inTree;   // whether the vertex has been added to the tree

        VertexState(int vertexCount) {
            parent = new int[vertexCount];
            key = new int[vertexCount];
            inTree = new boolean[vertexCount];
            Arrays.fill(key, Integer.MAX_VALUE);
            Arrays.fill(parent, -1);
            key[0] = 0; // start growing the tree from vertex 0
        }
    }

    @Override
    public MstResult computeMst(WeightedGraph graph) {
        int vertexCount = graph.vertexCount();
        VertexState state = new VertexState(vertexCount);

        for (int step = 0; step < vertexCount - 1; step++) {
            int next = nearestVertexOutsideTree(state);
            state.inTree[next] = true;
            relaxNeighbours(graph, state, next);
        }

        return buildResult(graph, state);
    }

    /** Returns the vertex outside the tree reachable by the cheapest edge. */
    private int nearestVertexOutsideTree(VertexState state) {
        int min = Integer.MAX_VALUE;
        int minIndex = -1;
        for (int v = 0; v < state.key.length; v++) {
            if (!state.inTree[v] && state.key[v] < min) {
                min = state.key[v];
                minIndex = v;
            }
        }
        return minIndex;
    }

    /** Lowers the candidate edge cost of every neighbour of {@code from}. */
    private void relaxNeighbours(WeightedGraph graph, VertexState state, int from) {
        for (int v = 0; v < graph.vertexCount(); v++) {
            if (graph.hasEdge(from, v)
                    && !state.inTree[v]
                    && graph.weight(from, v) < state.key[v]) {
                state.parent[v] = from;
                state.key[v] = graph.weight(from, v);
            }
        }
    }

    /** Translates the parent/key bookkeeping into a list of MST edges. */
    private MstResult buildResult(WeightedGraph graph, VertexState state) {
        List<Edge> edges = new ArrayList<>();
        for (int v = 1; v < graph.vertexCount(); v++) {
            int parent = state.parent[v];
            edges.add(new Edge(parent, v, graph.weight(parent, v)));
        }
        return new MstResult(edges);
    }
}
