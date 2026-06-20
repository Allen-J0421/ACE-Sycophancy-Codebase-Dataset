import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Fluent builder for {@link DirectedGraph}.
 *
 * <p>Vertices are registered the first time they are referenced (as either
 * endpoint of an edge, or explicitly via {@link #addVertex}), so callers never
 * have to declare the vertex count up front or reserve sentinel slots.
 *
 * <pre>{@code
 * DirectedGraph<Integer> g = new DirectedGraphBuilder<Integer>()
 *         .addEdge(1, 3)
 *         .addEdge(3, 2)
 *         .build();
 * }</pre>
 *
 * @param <V> the vertex label type
 */
public final class DirectedGraphBuilder<V> {

    private final Map<V, ArrayList<V>> adjacency = new LinkedHashMap<>();

    /**
     * Ensures a vertex exists in the graph, even if it has no outgoing edges.
     *
     * @param vertex the vertex to register
     * @return this builder, for chaining
     */
    public DirectedGraphBuilder<V> addVertex(V vertex) {
        adjacency.computeIfAbsent(vertex, k -> new ArrayList<>());
        return this;
    }

    /**
     * Adds a directed edge {@code from -> to}, registering either endpoint that
     * has not been seen before.
     *
     * @param from the source vertex
     * @param to   the destination vertex
     * @return this builder, for chaining
     */
    public DirectedGraphBuilder<V> addEdge(V from, V to) {
        addVertex(from);
        addVertex(to);
        adjacency.get(from).add(to);
        return this;
    }

    /**
     * Adds a directed edge described by an {@link Edge}.
     *
     * @param edge the edge to add
     * @return this builder, for chaining
     */
    public DirectedGraphBuilder<V> addEdge(Edge<V> edge) {
        return addEdge(edge.from(), edge.to());
    }

    /**
     * @return an immutable {@link DirectedGraph} containing the registered
     *         vertices and edges
     */
    public DirectedGraph<V> build() {
        return new DirectedGraph<>(new LinkedHashMap<>(adjacency));
    }
}
