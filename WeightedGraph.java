import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class WeightedGraph {

    record Arc(int to, int weight) {}

    private final int vertices;
    private final List<Arc>[] adjacency;
    private final int edgeCount;

    @SuppressWarnings("unchecked")
    private WeightedGraph(int vertices, List<Arc>[] adjacency, int edgeCount) {
        this.vertices = vertices;
        this.adjacency = adjacency;
        this.edgeCount = edgeCount;
    }

    static WeightedGraph from(int vertices, int[][] edges) {
        List<WeightedEdge> list = new ArrayList<>();
        for (int[] e : edges) {
            list.add(new WeightedEdge(e[0], e[1], e[2]));
        }
        return create(vertices, list);
    }

    static WeightedGraph of(int vertices, WeightedEdge... edges) {
        return create(vertices, List.of(edges));
    }

    @SuppressWarnings("unchecked")
    private static WeightedGraph create(int vertices, List<WeightedEdge> edges) {
        if (vertices <= 0) {
            throw new IllegalArgumentException("Vertex count must be positive, got: " + vertices);
        }
        List<Arc>[] adjacency = new List[vertices];
        for (int i = 0; i < vertices; i++) {
            adjacency[i] = new ArrayList<>();
        }
        for (WeightedEdge e : edges) {
            requireVertex(e.from(), vertices, "from");
            requireVertex(e.to(), vertices, "to");
            adjacency[e.from()].add(new Arc(e.to(), e.weight()));
        }
        return new WeightedGraph(vertices, adjacency, edges.size());
    }

    private static void requireVertex(int v, int vertices, String label) {
        if (v < 0 || v >= vertices) {
            throw new IllegalArgumentException(
                "Vertex " + label + "=" + v + " is out of range [0," + (vertices - 1) + "]");
        }
    }

    int vertices() { return vertices; }

    int edgeCount() { return edgeCount; }

    List<Arc> outgoing(int vertex) {
        return Collections.unmodifiableList(adjacency[vertex]);
    }

    List<WeightedEdge> edges() {
        List<WeightedEdge> result = new ArrayList<>(edgeCount);
        for (int u = 0; u < vertices; u++) {
            for (Arc arc : adjacency[u]) {
                result.add(new WeightedEdge(u, arc.to(), arc.weight()));
            }
        }
        return Collections.unmodifiableList(result);
    }
}
