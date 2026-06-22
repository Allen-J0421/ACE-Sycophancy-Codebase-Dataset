final class KruskalMinimumSpanningTreeCalculator implements MinimumSpanningTreeCalculator {
    @Override
    public MstResult compute(Graph graph) {
        DisjointSet disjointSet = new DisjointSet(graph.vertexCount());
        MstResultBuilder resultBuilder = new MstResultBuilder();

        for (Edge edge : graph.edgesSortedByWeight()) {
            if (!disjointSet.union(edge.from(), edge.to())) {
                continue;
            }

            resultBuilder.include(edge);
            if (resultBuilder.edgesUsed() == graph.requiredEdgeCount()) {
                return resultBuilder.build();
            }
        }

        MstResult result = resultBuilder.build();
        graph.validateSpanningTree(result);
        return result;
    }
}
