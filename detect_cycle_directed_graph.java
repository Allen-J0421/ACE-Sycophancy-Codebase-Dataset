class DetectCycle {
    public static void main(String[] args) {
        DirectedGraph graph = new DirectedGraph.Builder(4)
            .edge(0, 1)
            .edge(1, 2)
            .edge(2, 0)
            .edge(2, 3)
            .build();

        System.out.println(CycleDetector.hasCycle(graph));
    }
}
