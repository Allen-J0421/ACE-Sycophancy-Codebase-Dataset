package bipartite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An immutable undirected graph backed by an adjacency list. Vertices are the
 * integers in the range {@code [0, order())}.
 *
 * <p>Instances are created through the {@code of(...)} factory methods, which
 * validate their inputs and then freeze the adjacency structure so a graph
 * cannot be mutated after construction.
 */
public final class UndirectedGraph {

    private final int order;
    private final List<List<Integer>> adjacency;

    private UndirectedGraph(int order, List<List<Integer>> adjacency) {
        this.order = order;
        this.adjacency = adjacency;
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

        List<List<Integer>> adjacency = new ArrayList<>(order);
        for (int i = 0; i < order; i++) {
            adjacency.add(new ArrayList<>());
        }
        for (Edge edge : edges) {
            adjacency.get(edge.u()).add(edge.v());
            adjacency.get(edge.v()).add(edge.u());
        }

        return new UndirectedGraph(order, freeze(adjacency));
    }

    /**
     * Convenience overload that accepts edges as {@code {u, v}} pairs, matching
     * the classic competitive-programming input format.
     *
     * @param order the number of vertices; vertices are {@code [0, order)}
     * @param edges an array of two-element {@code {u, v}} arrays
     * @throws IllegalArgumentException if any pair does not have exactly two
     *                                  endpoints, or if the inputs are invalid
     */
    public static UndirectedGraph of(int order, int[][] edges) {
        if (edges == null) {
            throw new IllegalArgumentException("Edge array must not be null");
        }
        List<Edge> edgeList = new ArrayList<>(edges.length);
        for (int[] pair : edges) {
            if (pair == null || pair.length != 2) {
                throw new IllegalArgumentException(
                        "Each edge must have exactly two endpoints {u, v}");
            }
            edgeList.add(new Edge(pair[0], pair[1]));
        }
        return of(order, edgeList);
    }

    /** @return the number of vertices in the graph */
    public int order() {
        return order;
    }

    /**
     * @param vertex a vertex id in {@code [0, order())}
     * @return an unmodifiable view of the vertices adjacent to {@code vertex}
     * @throws IndexOutOfBoundsException if {@code vertex} is out of range
     */
    public List<Integer> neighbors(int vertex) {
        if (vertex < 0 || vertex >= order) {
            throw new IndexOutOfBoundsException(
                    "Vertex " + vertex + " is outside the range [0, " + order + ")");
        }
        return adjacency.get(vertex);
    }

    private static List<List<Integer>> freeze(List<List<Integer>> adjacency) {
        List<List<Integer>> frozen = new ArrayList<>(adjacency.size());
        for (List<Integer> neighbors : adjacency) {
            frozen.add(Collections.unmodifiableList(neighbors));
        }
        return Collections.unmodifiableList(frozen);
    }
}
