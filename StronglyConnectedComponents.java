public final class StronglyConnectedComponents {

    private StronglyConnectedComponents() {
    }

    public static void main(String[] args) {
        DirectedGraph graph = DirectedGraph.builder()
            .addVertexRange(5)
            .addEdge(Edge.of(0, 2))
            .addEdge(Edge.of(0, 3))
            .addEdge(Edge.of(1, 0))
            .addEdge(Edge.of(2, 1))
            .addEdge(Edge.of(3, 4))
            .build();

        StronglyConnectedComponentsFinder solver = new KosarajuStronglyConnectedComponentsFinder();
        StronglyConnectedComponentsResult result = solver.findComponents(graph);
        StronglyConnectedComponentsFormatter formatter = new StronglyConnectedComponentsFormatter();

        System.out.println(formatter.format(result));
    }
}
