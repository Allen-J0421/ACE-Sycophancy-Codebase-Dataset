final class GraphExamples {

    private GraphExamples() {
    }

    static UndirectedGraph createSampleGraph() {
        return UndirectedGraph.builder(6)
                .addEdge(1, 2)
                .addEdge(0, 3)
                .addEdge(2, 0)
                .addEdge(5, 4)
                .build();
    }
}
