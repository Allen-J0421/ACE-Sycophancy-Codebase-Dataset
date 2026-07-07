package graph.cycle;

public class DetectCycle {
    public static void main(String[] args) {
        DirectedGraph graph = DirectedGraph.builder(4)
                .addEdge(0, 1).addEdge(1, 2)
                .addEdge(2, 0).addEdge(2, 3)
                .build();

        CycleDetector detector = CycleDetectorFactory.create(Algorithm.KAHN);
        System.out.println(detector.hasCycle(graph));
    }
}
