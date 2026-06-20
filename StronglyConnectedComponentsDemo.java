public class StronglyConnectedComponentsDemo {

    public static void main(String[] args) {
        DirectedGraph graph = new DirectedGraphBuilder()
                .withVertexCount(5)
                .addEdge(1, 3)
                .addEdge(1, 4)
                .addEdge(2, 1)
                .addEdge(3, 2)
                .addEdge(4, 5)
                .build();

        StronglyConnectedComponentsFinder finder = new KosarajuStronglyConnectedComponents();
        StronglyConnectedComponentsResult result = finder.find(graph);

        System.out.println(StronglyConnectedComponentsFormatter.format(result));
    }
}
