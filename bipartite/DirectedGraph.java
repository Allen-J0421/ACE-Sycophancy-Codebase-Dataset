package bipartite;

import java.util.List;

/**
 * An immutable, unweighted directed graph. Each edge {@code u -> v} is stored
 * only on the source, so {@link #neighbors(int)} reports a vertex's
 * out-neighbors. This demonstrates how the generic adjacency-list base supports
 * a directed representation: the only difference from {@link UndirectedGraph} is
 * that no reciprocal incidence is added.
 *
 * <p>Note that bipartiteness is a property of undirected graphs;
 * {@link BipartiteChecker} is not meaningful on the directed out-adjacency.
 */
public final class DirectedGraph extends AbstractAdjacencyListGraph<Integer> {

    private DirectedGraph(int order) {
        super(order);
    }

    /**
     * Builds a directed graph from a vertex count and a list of edges, each read
     * as {@code u -> v}.
     *
     * @param order the number of vertices; vertices are {@code [0, order)}
     * @param edges the directed edges
     * @throws IllegalArgumentException if the inputs are invalid
     *                                  (see {@link GraphInputValidator})
     */
    public static DirectedGraph of(int order, List<Edge> edges) {
        GraphInputValidator.validate(order, edges);
        DirectedGraph graph = new DirectedGraph(order);
        for (Edge edge : edges) {
            graph.addIncidence(edge.u(), edge.v());
        }
        return graph;
    }

    /**
     * Convenience overload that accepts edges as {@code {u, v}} pairs, each read
     * as {@code u -> v}.
     */
    public static DirectedGraph of(int order, int[][] edges) {
        return of(order, Edge.fromPairs(edges));
    }

    @Override
    protected int targetOf(Integer incidence) {
        return incidence;
    }
}
