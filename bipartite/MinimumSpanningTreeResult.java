package bipartite;

import java.util.List;

/**
 * The outcome of a minimum-spanning-tree computation, modelled as a sealed type —
 * mirroring the other result types in this package — so callers handle exactly
 * the two possible cases and illegal states cannot be represented.
 *
 * <p>Either the graph is connected and a spanning {@linkplain Tree} is available
 * (its total weight and edges), or it is {@linkplain Disconnected}, in which case
 * no spanning tree exists and the vertices reachable from the start are reported
 * as a witness. Callers can branch with a switch:
 *
 * <pre>{@code
 * switch (new MinimumSpanningTree().compute(graph)) {
 *     case MinimumSpanningTreeResult.Tree t         -> use(t.totalWeight(), t.edges());
 *     case MinimumSpanningTreeResult.Disconnected d -> report(d.reachableVertices());
 * }
 * }</pre>
 */
public sealed interface MinimumSpanningTreeResult
        permits MinimumSpanningTreeResult.Tree, MinimumSpanningTreeResult.Disconnected {

    /** @return whether the graph is connected (and therefore has a spanning tree) */
    boolean isConnected();

    /**
     * Result for a connected graph.
     *
     * @param totalWeight the sum of the weights of the tree's edges
     * @param edges       the edges forming the minimum spanning tree; each is
     *                    oriented from the vertex already in the tree
     *                    ({@link WeightedEdge#u()}) to the vertex it adds
     *                    ({@link WeightedEdge#v()}). A graph of {@code n} vertices
     *                    yields {@code n - 1} edges (empty for 0 or 1 vertices).
     */
    record Tree(double totalWeight, List<WeightedEdge> edges) implements MinimumSpanningTreeResult {

        public Tree {
            edges = List.copyOf(edges);
        }

        @Override
        public boolean isConnected() {
            return true;
        }
    }

    /**
     * Result for a graph that is not connected, so no single tree can span it.
     *
     * @param reachableVertices the vertices reachable from the start vertex (the
     *                          component that a partial tree could cover), sorted
     *                          ascending
     * @param order             the total number of vertices in the graph
     */
    record Disconnected(List<Integer> reachableVertices, int order)
            implements MinimumSpanningTreeResult {

        public Disconnected {
            reachableVertices = List.copyOf(reachableVertices);
        }

        @Override
        public boolean isConnected() {
            return false;
        }
    }
}
