import java.util.List;

record MinimumSpanningTree(int totalWeight, List<Edge> edges) {
    MinimumSpanningTree {
        if (edges == null) {
            throw new IllegalArgumentException("Edges cannot be null.");
        }

        edges = List.copyOf(edges);
    }

    int edgeCount() {
        return edges.size();
    }
}
