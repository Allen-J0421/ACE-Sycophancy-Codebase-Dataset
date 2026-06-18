import java.util.List;
import java.util.StringJoiner;

class TopologicalSort {
    private static final KahnTopologicalSorter SORTER = new KahnTopologicalSorter();

    static List<Integer> topoSort(DirectedGraph graph) {
        return SORTER.sort(graph);
    }

    private static DirectedGraph buildSampleGraph() {
        return DirectedGraph.fromEdges(
                6,
                new int[][] {
                    {0, 1},
                    {1, 2},
                    {2, 3},
                    {4, 5},
                    {5, 1},
                    {5, 2}
                });
    }

    private static String formatOrder(List<Integer> order) {
        StringJoiner joiner = new StringJoiner(" ");
        for (int vertex : order) {
            joiner.add(String.valueOf(vertex));
        }
        return joiner.toString();
    }

    public static void main(String[] args) {
        DirectedGraph graph = buildSampleGraph();
        List<Integer> order = topoSort(graph);
        System.out.println(formatOrder(order));
    }
}
