final class GraphExamples {

    private GraphExamples() {
    }

    static UndirectedGraph createSampleGraph() {
        UndirectedGraph graph = new UndirectedGraph(6);
        graph.addEdge(1, 2);
        graph.addEdge(0, 3);
        graph.addEdge(2, 0);
        graph.addEdge(5, 4);
        return graph;
    }
}
