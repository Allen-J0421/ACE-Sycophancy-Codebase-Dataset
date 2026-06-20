public final class ConnectedComponentsDemo {

    private ConnectedComponentsDemo() {
    }

    public static void main(String[] args) {
        UndirectedGraph graph = UndirectedGraph.builder(6)
                .addEdge(1, 2)
                .addEdge(0, 3)
                .addEdge(2, 0)
                .addEdge(5, 4)
                .build();

        System.out.println(ComponentFormatter.formatComponents(ConnectedComponents.findComponents(graph)));
    }
}
