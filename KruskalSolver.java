final class KruskalSolver {
    MstResult compute(Graph graph) {
        DisjointSet disjointSet = new DisjointSet(graph.vertexCount());
        MstState state = new MstState();

        for (Edge edge : graph.edgesSortedByWeight()) {
            if (!disjointSet.union(edge.from(), edge.to())) {
                continue;
            }

            state.include(edge);

            if (state.edgesUsed() == graph.requiredEdgeCount()) {
                return state.toResult();
            }
        }

        MstResult result = state.toResult();
        graph.validateSpanningTree(result);
        return result;
    }

    private static final class MstState {
        private int totalWeight;
        private int edgesUsed;

        private void include(Edge edge) {
            totalWeight += edge.weight();
            edgesUsed++;
        }

        private int edgesUsed() {
            return edgesUsed;
        }

        private MstResult toResult() {
            return new MstResult(totalWeight, edgesUsed);
        }
    }
}
