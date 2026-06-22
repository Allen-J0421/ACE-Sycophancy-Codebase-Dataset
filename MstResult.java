import java.util.List;
import java.util.Objects;

class MstResult {
    private final List<Edge> edges;
    private final int totalWeight;

    MstResult(List<Edge> edges) {
        this.edges = List.copyOf(edges);
        this.totalWeight = this.edges.stream().mapToInt(Edge::weight).sum();
    }

    List<Edge> edges() {
        return edges;
    }

    int totalWeight() {
        return totalWeight;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof MstResult)) return false;
        MstResult other = (MstResult) obj;
        return totalWeight == other.totalWeight && edges.equals(other.edges);
    }

    @Override
    public int hashCode() {
        return Objects.hash(totalWeight, edges);
    }

    @Override
    public String toString() {
        return "MstResult{edges=" + edges + ", totalWeight=" + totalWeight + "}";
    }
}
