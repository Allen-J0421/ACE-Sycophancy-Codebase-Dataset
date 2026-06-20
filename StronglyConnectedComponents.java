public final class StronglyConnectedComponents {

    private StronglyConnectedComponents() {
    }

    public static void main(String[] args) {
        DirectedGraph graph = DirectedGraph.fromEdges(
            5,
            new int[][] {
                {0, 2}, {0, 3}, {1, 0}, {2, 1}, {3, 4}
            }
        );

        StronglyConnectedComponentsFinder solver = new KosarajuStronglyConnectedComponentsFinder();
        StronglyConnectedComponentsResult result = solver.findComponents(graph);

        System.out.println(result);
    }
}
