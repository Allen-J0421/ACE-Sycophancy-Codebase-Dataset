import java.util.List;

public final class DepthFirstSearchDemo {

    private DepthFirstSearchDemo() {
        // Utility class.
    }

    public static void main(String[] args) {
        Graph graph = Graph.fromUndirectedEdges(
                6,
                new int[][] {
                    {1, 2},
                    {0, 3},
                    {2, 0},
                    {5, 4}
                });

        List<Integer> traversal = DepthFirstSearch.traverse(graph);
        for (int vertex : traversal) {
            System.out.print(vertex + " ");
        }
    }
}
