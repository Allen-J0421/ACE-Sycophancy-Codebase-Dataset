import java.util.List;
import java.util.StringJoiner;

public final class TopologicalSort {
    private static final int SAMPLE_GRAPH_VERTEX_COUNT = 6;
    private static final int[][] SAMPLE_GRAPH_EDGES = {
        {0, 1},
        {1, 2},
        {2, 3},
        {4, 5},
        {5, 1},
        {5, 2}
    };

    private static final KahnTopologicalSorter SORTER = new KahnTopologicalSorter();

    private TopologicalSort() {
    }

    public static List<Integer> sort(DirectedGraph graph) {
        return SORTER.sort(graph);
    }

    public static void main(String[] args) {
        DirectedGraph graph = DirectedGraph.fromEdges(SAMPLE_GRAPH_VERTEX_COUNT, SAMPLE_GRAPH_EDGES);
        List<Integer> order = sort(graph);
        System.out.println(formatOrder(order));
    }

    private static String formatOrder(List<Integer> order) {
        StringJoiner joiner = new StringJoiner(" ");
        for (int vertex : order) {
            joiner.add(String.valueOf(vertex));
        }
        return joiner.toString();
    }
}
