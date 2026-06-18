import java.util.ArrayList;
import java.util.List;

public final class TopologicalSortDemo {

    private static final int SAMPLE_VERTEX_COUNT = 6;
    private static final int[][] SAMPLE_EDGES = {
        {0, 1},
        {1, 2},
        {2, 3},
        {4, 5},
        {5, 1},
        {5, 2}
    };

    private TopologicalSortDemo() {
        // Utility class.
    }

    private static List<List<Integer>> newGraph(int vertices) {
        ArrayList<List<Integer>> graph = new ArrayList<>(vertices);
        for (int vertex = 0; vertex < vertices; vertex++) {
            graph.add(new ArrayList<>());
        }
        return graph;
    }

    private static void buildSampleGraph(List<List<Integer>> graph) {
        for (int[] edge : SAMPLE_EDGES) {
            TopologicalSort.addEdge(graph, edge[0], edge[1]);
        }
    }

    private static void printOrder(List<Integer> order) {
        for (int vertex : order) {
            System.out.print(vertex + " ");
        }
        System.out.println();
    }

    public static void main(String[] args) {
        List<List<Integer>> graph = newGraph(SAMPLE_VERTEX_COUNT);
        buildSampleGraph(graph);
        printOrder(TopologicalSort.topologicalSort(graph));
    }
}
