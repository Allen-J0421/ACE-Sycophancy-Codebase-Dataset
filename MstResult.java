record MstResult(int totalWeight, int edgesUsed) {
    static MstResult empty() {
        return new MstResult(0, 0);
    }

    MstResult include(Edge edge) {
        return new MstResult(totalWeight + edge.weight(), edgesUsed + 1);
    }

    boolean spans(Graph graph) {
        return edgesUsed == graph.requiredEdgeCount();
    }
}
