package bipartite;

import java.util.List;

/**
 * An immutable, unweighted undirected graph. Each edge {@code {u, v}} is stored
 * symmetrically, so {@code v} appears among {@code u}'s neighbors and vice
 * versa. Built through the {@code of(...)} factory methods, which validate their
 * inputs before construction.
 */
public final class UndirectedGraph extends AbstractAdjacencyListGraph<Integer> {

    private UndirectedGraph(int order) {
        super(order);
    }

    /**
     * Builds a graph from a vertex count and a list of edges.
     *
     * @param order the number of vertices; vertices are {@code [0, order)}
     * @param edges the undirected edges connecting the vertices
     * @throws IllegalArgumentException if the inputs are invalid
     *                                  (see {@link GraphInputValidator})
     */
    public static UndirectedGraph of(int order, List<Edge> edges) {
        GraphInputValidator.validate(order, edges);
        UndirectedGraph graph = new UndirectedGraph(order);
        for (Edge edge : edges) {
            graph.addIncidence(edge.u(), edge.v());
            graph.addIncidence(edge.v(), edge.u());
        }
        return graph;
    }

    /**
     * Convenience overload that accepts edges as {@code {u, v}} pairs.
     *
     * @param order the number of vertices; vertices are {@code [0, order)}
     * @param edges an array of two-element {@code {u, v}} arrays
     * @throws IllegalArgumentException if any pair is malformed or the inputs
     *                                  are otherwise invalid
     */
    public static UndirectedGraph of(int order, int[][] edges) {
        return of(order, Edge.fromPairs(edges));
    }

    @Override
    protected int targetOf(Integer incidence) {
        return incidence;
    }
}
