import java.util.List;

record MstResult(List<Edge> edges, int totalWeight) {
    MstResult {
        edges = List.copyOf(edges);
    }

    static MstResult fromEdges(List<Edge> edges) {
        return new MstResult(edges, calculateTotalWeight(edges));
    }

    private static int calculateTotalWeight(List<Edge> edges) {
        int totalWeight = 0;
        for (Edge edge : edges) {
            totalWeight += edge.weight();
        }
        return totalWeight;
    }
}
