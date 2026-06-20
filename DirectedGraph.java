import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * An immutable directed graph backed by adjacency lists.
 *
 * <p>Unlike the original implementation, which stored a dense {@code V x V}
 * matrix padded with {@code -1} sentinels, this representation keeps one
 * adjacency list per vertex. Memory usage is {@code O(V + E)} and the
 * structure imposes no constraint on how vertices are labelled, so the
 * "phantom vertex 0" required by the old 1-indexed code is no longer needed.
 *
 * <p>Vertices are kept in insertion order so that traversals and output are
 * deterministic. Instances are created through {@link DirectedGraphBuilder}.
 *
 * @param <V> the vertex label type
 */
public final class DirectedGraph<V> {

    private final Map<V, List<V>> adjacency;

    DirectedGraph(Map<V, ? extends List<V>> adjacency) {
        // Defensive copy preserving insertion order; lists made unmodifiable.
        Map<V, List<V>> copy = new LinkedHashMap<>();
        for (Map.Entry<V, ? extends List<V>> entry : adjacency.entrySet()) {
            copy.put(entry.getKey(), Collections.unmodifiableList(new ArrayList<>(entry.getValue())));
        }
        this.adjacency = Collections.unmodifiableMap(copy);
    }

    /** @return the vertices of this graph, in insertion order. */
    public Set<V> vertices() {
        return adjacency.keySet();
    }

    /** @return the number of vertices. */
    public int vertexCount() {
        return adjacency.size();
    }

    /**
     * @param vertex a vertex of this graph
     * @return the vertices directly reachable from {@code vertex} via one edge
     * @throws IllegalArgumentException if the vertex is not in the graph
     */
    public List<V> successors(V vertex) {
        List<V> out = adjacency.get(vertex);
        if (out == null) {
            throw new IllegalArgumentException("Unknown vertex: " + vertex);
        }
        return out;
    }

    /**
     * Returns the transpose (edge-reversed) graph. Kosaraju's algorithm runs
     * its second pass over this graph.
     *
     * @return a new graph with every edge direction reversed
     */
    public DirectedGraph<V> transpose() {
        Map<V, List<V>> reversed = new LinkedHashMap<>();
        for (V vertex : adjacency.keySet()) {
            reversed.put(vertex, new ArrayList<>());
        }
        for (Map.Entry<V, List<V>> entry : adjacency.entrySet()) {
            V from = entry.getKey();
            for (V to : entry.getValue()) {
                reversed.get(to).add(from);
            }
        }
        return new DirectedGraph<>(reversed);
    }
}
