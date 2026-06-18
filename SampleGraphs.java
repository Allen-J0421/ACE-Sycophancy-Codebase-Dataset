final class SampleGraphs {
    private SampleGraphs() {
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
}
