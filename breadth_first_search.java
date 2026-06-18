import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class BreadthFirstSearch {
    private static final int SAMPLE_VERTEX_COUNT = 6;
    private static final List<Edge> SAMPLE_EDGES = Collections.unmodifiableList(Arrays.asList(
            new Edge(1, 2),
            new Edge(2, 0),
            new Edge(0, 3),
            new Edge(4, 5)
    ));

    public static void main(String[] args) {
        UndirectedGraph graph = createSampleGraph();
        printTraversal(BreadthFirstTraversal.traverse(graph));
    }

    private static UndirectedGraph createSampleGraph() {
        return UndirectedGraph.fromEdges(SAMPLE_VERTEX_COUNT, SAMPLE_EDGES);
    }

    private static void printTraversal(List<Integer> traversal) {
        for (int vertex : traversal) {
            System.out.print(vertex + " ");
        }
    }
}
