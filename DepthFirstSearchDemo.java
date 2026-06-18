import java.util.List;

public final class DepthFirstSearchDemo {

    private DepthFirstSearchDemo() {
        // Utility class.
    }

    public static void main(String[] args) {
        Graph graph = Graph.undirected(6);

        graph.addEdge(1, 2);
        graph.addEdge(0, 3);
        graph.addEdge(2, 0);
        graph.addEdge(5, 4);

        List<Integer> traversal = DepthFirstSearch.traverse(graph);
        for (int vertex : traversal) {
            System.out.print(vertex + " ");
        }
    }
}
