import java.util.Collections;
import java.util.List;

/**
 * The result of a minimum-spanning-tree computation: the chosen edges together
 * with their cumulative weight. The edge list is unmodifiable.
 */
public final class MstResult {

    private final List<Edge> edges;

    public MstResult(List<Edge> edges) {
        this.edges = List.copyOf(edges);
    }

    /** Returns the edges that make up the spanning tree, in discovery order. */
    public List<Edge> edges() {
        return Collections.unmodifiableList(edges);
    }

    /** Returns the total weight of the spanning tree. */
    public int totalWeight() {
        int total = 0;
        for (Edge edge : edges) {
            total += edge.weight();
        }
        return total;
    }
}
