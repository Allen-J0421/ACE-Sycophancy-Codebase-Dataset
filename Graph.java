import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

final class Graph<V> {
    private final Map<V, List<V>> adjacencyMap;

    private Graph(Map<V, List<V>> adjacencyMap) {
        this.adjacencyMap = adjacencyMap;
    }

    Set<V> vertices() {
        return adjacencyMap.keySet();
    }

    int vertexCount() {
        return adjacencyMap.size();
    }

    List<V> neighbors(V vertex) {
        List<V> neighbors = adjacencyMap.get(vertex);
        if (neighbors == null) {
            throw new IllegalArgumentException("Vertex " + vertex + " is not in this graph");
        }
        return neighbors;
    }

    static final class Builder<V> {
        private final Map<V, List<V>> adjacencyMap = new LinkedHashMap<>();

        Builder<V> addVertex(V vertex) {
            Objects.requireNonNull(vertex, "vertex must not be null");
            adjacencyMap.putIfAbsent(vertex, new ArrayList<>());
            return this;
        }

        Builder<V> addEdge(V u, V v) {
            Objects.requireNonNull(u, "u must not be null");
            Objects.requireNonNull(v, "v must not be null");
            adjacencyMap.computeIfAbsent(u, k -> new ArrayList<>()).add(v);
            adjacencyMap.computeIfAbsent(v, k -> new ArrayList<>()).add(u);
            return this;
        }

        Graph<V> build() {
            Map<V, List<V>> frozen = new LinkedHashMap<>();
            for (Map.Entry<V, List<V>> entry : adjacencyMap.entrySet()) {
                frozen.put(entry.getKey(), Collections.unmodifiableList(new ArrayList<>(entry.getValue())));
            }
            return new Graph<>(Collections.unmodifiableMap(frozen));
        }
    }
}
