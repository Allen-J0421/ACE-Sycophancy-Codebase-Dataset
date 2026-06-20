public final class DetectCycle {
    private DetectCycle() {
    }

    public static void main(String[] args) {
        DirectedGraph graph = sampleGraph();

        System.out.println(CycleDetector.hasCycle(graph));
    }

    private static DirectedGraph sampleGraph() {
        return DirectedGraph.builder(4)
                .addEdge(0, 1)
                .addEdge(1, 2)
                .addEdge(2, 0)
                .addEdge(2, 3)
                .build();
    }
}
