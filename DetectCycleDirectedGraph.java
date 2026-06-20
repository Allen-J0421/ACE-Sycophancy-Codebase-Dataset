import java.util.List;

public final class DetectCycleDirectedGraph {
    private static final int SAMPLE_VERTEX_COUNT = 4;
    private static final List<DirectedEdge> SAMPLE_EDGES = List.of(
            new DirectedEdge(0, 1),
            new DirectedEdge(1, 2),
            new DirectedEdge(2, 0),
            new DirectedEdge(2, 3));

    private DetectCycleDirectedGraph() {
    }

    public static boolean hasCycle(DirectedGraph graph) {
        return CycleDetector.hasCycle(graph);
    }

    public static boolean hasCycle(List<List<Integer>> adjacencyList) {
        return hasCycle(DirectedGraph.fromAdjacencyList(adjacencyList));
    }

    private static DirectedGraph createSampleGraph() {
        return DirectedGraph.fromEdges(SAMPLE_VERTEX_COUNT, SAMPLE_EDGES);
    }

    public static void main(String[] args) {
        DirectedGraph graph = createSampleGraph();
        System.out.println(graph.hasCycle());
    }
}
