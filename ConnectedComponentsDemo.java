import java.util.List;

public final class ConnectedComponentsDemo {

    private ConnectedComponentsDemo() {
    }

    public static void main(String[] args) {
        UndirectedGraph graph = UndirectedGraph.withVertexCount(6);

        graph.addEdge(1, 2);
        graph.addEdge(0, 3);
        graph.addEdge(2, 0);
        graph.addEdge(5, 4);

        printComponents(ConnectedComponents.getComponents(graph));
    }

    private static void printComponents(List<List<Integer>> components) {
        for (List<Integer> component : components) {
            System.out.println(formatComponent(component));
        }
    }

    private static String formatComponent(List<Integer> component) {
        StringBuilder line = new StringBuilder();
        for (int vertex : component) {
            if (line.length() > 0) {
                line.append(' ');
            }
            line.append(vertex);
        }
        return line.toString();
    }
}
