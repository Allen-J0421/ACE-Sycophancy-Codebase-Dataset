public final class CycleDetector {
    private CycleDetector() {
    }

    public static boolean hasCycle(DirectedGraphView graph) {
        return TopologicalSorter.sort(graph).size() != graph.vertexCount();
    }
}
