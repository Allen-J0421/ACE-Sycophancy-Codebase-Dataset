import java.util.List;

record MinimumSpanningTreeResult(int totalWeight, List<Edge> edges, boolean connected) {
    MinimumSpanningTreeResult {
        if (edges == null) {
            throw new IllegalArgumentException("Edges must not be null.");
        }

        edges = List.copyOf(edges);
    }

    boolean isConnected() {
        return connected;
    }
}
