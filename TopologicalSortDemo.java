import java.util.Arrays;
import java.util.List;

public final class TopologicalSortDemo {
    private static final int SAMPLE_GRAPH_VERTEX_COUNT = 6;
    private static final List<DirectedEdge> SAMPLE_GRAPH_EDGES = Arrays.asList(
            DirectedEdge.of(0, 1),
            DirectedEdge.of(1, 2),
            DirectedEdge.of(2, 3),
            DirectedEdge.of(4, 5),
            DirectedEdge.of(5, 1),
            DirectedEdge.of(5, 2));

    private TopologicalSortDemo() {
    }

    public static void main(String[] args) {
        DirectedGraph graph = DirectedGraph.fromEdges(SAMPLE_GRAPH_VERTEX_COUNT, SAMPLE_GRAPH_EDGES);
        List<Integer> order = TopologicalSort.sort(graph);
        System.out.println(TopologicalOrderFormatter.format(order));
    }
}
