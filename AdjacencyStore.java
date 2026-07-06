import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class AdjacencyStore {

    record Arc(int to, int weight) {}

    private final List<Arc>[] adjacency;
    private final int edgeCount;

    @SuppressWarnings("unchecked")
    AdjacencyStore(int size, List<WeightedEdge> edges, VertexIndex index) {
        List<Arc>[] adj = new List[size];
        for (int i = 0; i < size; i++) {
            adj[i] = new ArrayList<>();
        }
        for (WeightedEdge e : edges) {
            index.validate(e.from(), "from");
            index.validate(e.to(), "to");
            adj[e.from()].add(new Arc(e.to(), e.weight()));
        }
        this.adjacency = adj;
        this.edgeCount = edges.size();
    }

    int edgeCount() {
        return edgeCount;
    }

    List<Arc> outgoing(int vertex) {
        return Collections.unmodifiableList(adjacency[vertex]);
    }

    List<WeightedEdge> allEdges() {
        List<WeightedEdge> result = new ArrayList<>(edgeCount);
        for (int u = 0; u < adjacency.length; u++) {
            for (Arc arc : adjacency[u]) {
                result.add(new WeightedEdge(u, arc.to(), arc.weight()));
            }
        }
        return Collections.unmodifiableList(result);
    }
}
