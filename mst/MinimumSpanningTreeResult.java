package mst;

import java.util.List;

public record MinimumSpanningTreeResult(int totalWeight, List<Edge> edges, boolean connected) {
    public MinimumSpanningTreeResult {
        if (edges == null) {
            throw new IllegalArgumentException("Edges must not be null.");
        }

        edges = List.copyOf(edges);
    }

    public boolean isConnected() {
        return connected;
    }
}
