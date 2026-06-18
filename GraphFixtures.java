final class GraphFixtures {
    private GraphFixtures() {
    }

    static Graph weightedUndirectedExample() {
        return GraphBuilder.withVertexCount(5)
            .addUndirectedEdge(0, 1, 4)
            .addUndirectedEdge(0, 2, 8)
            .addUndirectedEdge(1, 4, 6)
            .addUndirectedEdge(1, 2, 3)
            .addUndirectedEdge(2, 3, 2)
            .addUndirectedEdge(3, 4, 10)
            .build();
    }

    static Graph threeVertexChain() {
        return GraphBuilder.withVertexCount(3)
            .addUndirectedEdge(0, 1, 2)
            .addUndirectedEdge(1, 2, 1)
            .build();
    }

    static Graph threeVertexGraphWithUnreachableTail() {
        return GraphBuilder.withVertexCount(3)
            .addUndirectedEdge(0, 1, 5)
            .build();
    }
}
