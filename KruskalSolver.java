final class KruskalSolver {
    MstResult compute(Graph graph) {
        DisjointSet disjointSet = new DisjointSet(graph.vertexCount());
        int totalWeight = 0;
        int edgesUsed = 0;

        for (Edge edge : graph.edgesSortedByWeight()) {
            if (!disjointSet.union(edge.from(), edge.to())) {
                continue;
            }

            totalWeight += edge.weight();
            edgesUsed++;

            if (edgesUsed == graph.requiredEdgeCount()) {
                return new MstResult(totalWeight, edgesUsed);
            }
        }

        MstResult result = new MstResult(totalWeight, edgesUsed);
        if (graph.isTriviallyConnected() || result.spans(graph)) {
            return result;
        }

        throw new IllegalArgumentException("Input graph must be connected to form an MST.");
    }
}
