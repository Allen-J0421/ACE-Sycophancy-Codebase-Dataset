import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class MinimumSpanningTreeResult {
    private final int totalWeight;
    private final List<Edge> edges;
    private final boolean connected;

    MinimumSpanningTreeResult(int totalWeight, List<Edge> edges, boolean connected) {
        if (edges == null) {
            throw new IllegalArgumentException("Edges must not be null.");
        }

        this.totalWeight = totalWeight;
        this.edges = Collections.unmodifiableList(new ArrayList<>(edges));
        this.connected = connected;
    }

    int totalWeight() {
        return totalWeight;
    }

    List<Edge> edges() {
        return edges;
    }

    boolean isConnected() {
        return connected;
    }
}
