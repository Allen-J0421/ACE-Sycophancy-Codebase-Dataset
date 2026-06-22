import java.util.Collections;
import java.util.List;

class MstResult {
    private final List<Edge> edges;
    private final int totalWeight;

    MstResult(List<Edge> edges) {
        this.edges = Collections.unmodifiableList(edges);
        this.totalWeight = edges.stream().mapToInt(e -> e.weight).sum();
    }

    List<Edge> edges() {
        return edges;
    }

    int totalWeight() {
        return totalWeight;
    }

    void print() {
        System.out.println("Edge \tWeight");
        for (Edge edge : edges) {
            System.out.println(edge);
        }
        System.out.println("Total weight: " + totalWeight);
    }
}
