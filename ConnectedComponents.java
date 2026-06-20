import java.util.List;

public final class ConnectedComponents {

    private ConnectedComponents() {
    }

    public static void main(String[] args) {
        UndirectedGraph graph = new UndirectedGraph(6);
        graph.addEdge(1, 2);
        graph.addEdge(0, 3);
        graph.addEdge(2, 0);
        graph.addEdge(5, 4);

        List<List<Integer>> components = GraphComponentFinder.findConnectedComponents(graph);
        printComponents(components);
    }

    private static void printComponents(List<List<Integer>> components) {
        for (List<Integer> component : components) {
            for (int vertex : component) {
                System.out.print(vertex + " ");
            }
            System.out.println();
        }
    }
}
