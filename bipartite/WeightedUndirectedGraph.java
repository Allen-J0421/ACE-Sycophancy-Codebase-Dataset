package bipartite;

import java.util.List;

/**
 * An immutable, weighted undirected graph. Each edge {@code {u, v}} with a
 * weight is stored symmetrically as two oriented incidences, so both endpoints
 * see the connection and its weight.
 *
 * <p>This implementation exercises the generic parameter of
 * {@link AbstractAdjacencyListGraph}: its incidence type is {@link WeightedEdge}
 * (carrying the weight) rather than a plain vertex id. Because it still exposes
 * the {@link Graph} interface — {@link #neighbors(int)} drops the weights —
 * weight-agnostic algorithms such as {@link BipartiteChecker} run on it
 * unchanged, while {@link #weightedNeighbors(int)} gives weight-aware callers the
 * full incidence data.
 */
public final class WeightedUndirectedGraph extends AbstractAdjacencyListGraph<WeightedEdge> {

    private WeightedUndirectedGraph(int order) {
        super(order);
    }

    /**
     * Builds a weighted graph from a vertex count and a list of weighted edges.
     *
     * @param order the number of vertices; vertices are {@code [0, order)}
     * @param edges the weighted undirected edges
     * @throws IllegalArgumentException if the inputs are invalid
     *                                  (see {@link GraphInputValidator})
     */
    public static WeightedUndirectedGraph of(int order, List<WeightedEdge> edges) {
        GraphInputValidator.validate(order, edges);
        WeightedUndirectedGraph graph = new WeightedUndirectedGraph(order);
        for (WeightedEdge edge : edges) {
            // Store each undirected edge as two oriented incidences, so the
            // incidence read from a vertex always has that vertex as its source.
            graph.addIncidence(edge.u(), edge);
            graph.addIncidence(edge.v(), new WeightedEdge(edge.v(), edge.u(), edge.weight()));
        }
        return graph;
    }

    /**
     * @param vertex a vertex id in {@code [0, order())}
     * @return the weighted edges leaving {@code vertex}; each has {@code vertex}
     *         as its {@link WeightedEdge#u() source}
     * @throws IndexOutOfBoundsException if {@code vertex} is out of range
     */
    public List<WeightedEdge> weightedNeighbors(int vertex) {
        return incidences(vertex);
    }

    @Override
    protected int targetOf(WeightedEdge incidence) {
        return incidence.v();
    }
}
