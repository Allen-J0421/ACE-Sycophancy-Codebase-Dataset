final class SampleGraphs {
    private SampleGraphs() {
    }

    static Graph disconnectedUndirectedGraph() {
        MutableGraph graph = new AdjacencyListGraph(6);
        graph.addUndirectedEdge(1, 2);
        graph.addUndirectedEdge(2, 0);
        graph.addUndirectedEdge(0, 3);
        graph.addUndirectedEdge(4, 5);
        return graph;
    }
}
