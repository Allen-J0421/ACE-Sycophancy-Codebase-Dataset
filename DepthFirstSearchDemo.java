import java.util.List;

public final class DepthFirstSearchDemo {

    private DepthFirstSearchDemo() {
        // Utility class.
    }

    public static void main(String[] args) {
        Graph graph = Graph.builder(6)
                .addUndirectedEdge(1, 2)
                .addUndirectedEdge(0, 3)
                .addUndirectedEdge(2, 0)
                .addUndirectedEdge(5, 4)
                .build();

        List<Integer> traversal = DepthFirstSearch.traverse(graph);
        for (int vertex : traversal) {
            System.out.print(vertex + " ");
        }
    }
}
