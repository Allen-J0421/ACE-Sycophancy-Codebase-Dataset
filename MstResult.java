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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof MstResult)) return false;
        MstResult other = (MstResult) obj;
        return totalWeight == other.totalWeight && edges.equals(other.edges);
    }

    @Override
    public int hashCode() {
        return 31 * totalWeight + edges.hashCode();
    }
}
