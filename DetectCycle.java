public final class DetectCycle {
    private DetectCycle() {
    }

    public static void main(String[] args) {
        DirectedGraph graph = sampleGraph();

        System.out.println(CycleDetector.hasCycle(graph));
    }

    private static DirectedGraph sampleGraph() {
        DirectedGraph graph = DirectedGraph.withVertexCount(4);
        graph.addEdge(0, 1);
        graph.addEdge(1, 2);
        graph.addEdge(2, 0);
        graph.addEdge(2, 3);
        return graph;
    }
}
