import java.util.List;

public final class TopologicalSortDemo {
    private TopologicalSortDemo() {
    }

    public static void main(String[] args) {
        DirectedGraph graph = SampleGraphs.sampleDag();
        List<Integer> order = TopologicalSort.sort(graph);
        System.out.println(TopologicalOrderFormatter.format(order));
    }
}
