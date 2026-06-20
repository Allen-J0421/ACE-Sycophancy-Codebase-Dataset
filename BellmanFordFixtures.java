final class BellmanFordFixtures {
    static final class Case {
        private final WeightedGraph graph;
        private final int source;
        private final int[] expectedDistances;

        private Case(WeightedGraph graph, int source, int[] expectedDistances) {
            this.graph = graph;
            this.source = source;
            this.expectedDistances = expectedDistances;
        }

        WeightedGraph graph() {
            return graph;
        }

        int source() {
            return source;
        }

        int[][] edgeData() {
            return graph.edgeData();
        }

        int[] expectedDistances() {
            return expectedDistances.clone();
        }
    }

    private BellmanFordFixtures() {
    }

    static Case sampleCase() {
        return new Case(
            WeightedGraph.of(
                5,
                WeightedEdge.of(1, 3, 2),
                WeightedEdge.of(4, 3, -1),
                WeightedEdge.of(2, 4, 1),
                WeightedEdge.of(1, 2, 1),
                WeightedEdge.of(0, 1, 5)
            ),
            0,
            new int[] {0, 5, 6, 6, 7}
        );
    }

    static Case negativeCycleCase() {
        return new Case(
            WeightedGraph.of(
                3,
                WeightedEdge.of(0, 1, 1),
                WeightedEdge.of(1, 2, -1),
                WeightedEdge.of(2, 0, -1)
            ),
            0,
            new int[] {-1}
        );
    }

    static Case unreachableVertexCase() {
        return new Case(
            WeightedGraph.of(
                4,
                WeightedEdge.of(0, 1, 4),
                WeightedEdge.of(1, 2, 3)
            ),
            0,
            new int[] {0, 4, 7, 100_000_000}
        );
    }
}
