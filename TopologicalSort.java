import java.util.List;

public final class TopologicalSort {
    private static final TopologicalSorter SORTER = new KahnTopologicalSorter();

    private TopologicalSort() {
    }

    public static List<Integer> sort(DirectedGraph graph) {
        return SORTER.sort(graph);
    }
}
