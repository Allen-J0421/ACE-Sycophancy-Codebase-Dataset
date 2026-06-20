final class BellmanFordFixtures {
    private BellmanFordFixtures() {
    }

    static WeightedGraph sampleGraph() {
        return WeightedGraph.of(
            5,
            WeightedEdge.of(1, 3, 2),
            WeightedEdge.of(4, 3, -1),
            WeightedEdge.of(2, 4, 1),
            WeightedEdge.of(1, 2, 1),
            WeightedEdge.of(0, 1, 5)
        );
    }

    static int[][] sampleEdgeData() {
        return new int[][] {
            {1, 3, 2},
            {4, 3, -1},
            {2, 4, 1},
            {1, 2, 1},
            {0, 1, 5}
        };
    }

    static WeightedGraph negativeCycleGraph() {
        return WeightedGraph.of(
            3,
            WeightedEdge.of(0, 1, 1),
            WeightedEdge.of(1, 2, -1),
            WeightedEdge.of(2, 0, -1)
        );
    }
}
