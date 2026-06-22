record MstResult(int totalWeight, int edgesUsed) {
    boolean spans(Graph graph) {
        return edgesUsed == graph.requiredEdgeCount();
    }
}
