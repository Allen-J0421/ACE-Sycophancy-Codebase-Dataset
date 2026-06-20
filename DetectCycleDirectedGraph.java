import java.util.List;

public final class DetectCycleDirectedGraph {

    private DetectCycleDirectedGraph() {
    }

    public static boolean hasCycle(DirectedGraph graph) {
        return CycleDetector.hasCycle(graph);
    }

    public static boolean hasCycle(List<List<Integer>> adjacencyList) {
        return hasCycle(DirectedGraph.fromAdjacencyList(adjacencyList));
    }

    private static DirectedGraph createSampleGraph() {
        return DirectedGraph.builder(4)
                .addEdge(0, 1)
                .addEdge(1, 2)
                .addEdge(2, 0)
                .addEdge(2, 3)
                .build();
    }

    public static void main(String[] args) {
        DirectedGraph graph = createSampleGraph();
        System.out.println(graph.hasCycle());
    }
}
