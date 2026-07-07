package graph.cycle;

public class DetectCycle {
    public static void main(String[] args) {
        DirectedGraph graph = new DirectedGraph(4);
        graph.addEdge(0, 1);
        graph.addEdge(1, 2);
        graph.addEdge(2, 0);
        graph.addEdge(2, 3);

        CycleDetector detector = CycleDetectorFactory.create(Algorithm.KAHN);
        System.out.println(detector.hasCycle(graph));
    }
}
