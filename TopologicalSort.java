public final class TopologicalSort {
    private static final TopologicalSorter SORTER = new KahnTopologicalSorter();

    private TopologicalSort() {
    }

    public static TopologicalOrder sort(DirectedGraph graph) {
        return SORTER.sort(graph);
    }
}
