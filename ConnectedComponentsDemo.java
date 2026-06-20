public final class ConnectedComponentsDemo {

    private ConnectedComponentsDemo() {
    }

    public static void main(String[] args) {
        UndirectedGraph graph = UndirectedGraph.withVertexCount(6);

        graph.addEdge(1, 2);
        graph.addEdge(0, 3);
        graph.addEdge(2, 0);
        graph.addEdge(5, 4);

        System.out.println(ComponentFormatter.formatComponents(ConnectedComponents.getComponents(graph)));
    }
}
