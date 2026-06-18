final class SampleGraphs {
    private SampleGraphs() {
    }

    static WeightedGraph demoGraph() {
        WeightedGraph graph = WeightedGraph.withVertexCount(5);

        graph.addUndirectedEdge(0, 1, 4);
        graph.addUndirectedEdge(0, 2, 8);
        graph.addUndirectedEdge(1, 4, 6);
        graph.addUndirectedEdge(1, 2, 3);
        graph.addUndirectedEdge(2, 3, 2);
        graph.addUndirectedEdge(3, 4, 10);

        return graph;
    }
}
