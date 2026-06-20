class DetectCycle {
    public static void main(String[] args) {
        Graph graph = new AdjacencyListGraph.Builder(4)
            .edge(0, 1)
            .edge(1, 2)
            .edge(2, 0)
            .edge(2, 3)
            .build();

        CycleDetector detector = new KahnCycleDetector();
        System.out.println(detector.hasCycle(graph));
    }
}
