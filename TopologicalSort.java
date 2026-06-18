import java.util.List;
import java.util.Collections;

public final class TopologicalSort {
    private static final TopologicalSorter SORTER = new KahnTopologicalSorter();

    private TopologicalSort() {
    }

    public static List<Integer> sort(DirectedGraph graph) {
        return Collections.unmodifiableList(SORTER.sort(graph));
    }
}
