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

    private static List<List<Integer>> createGraph(int vertices) {
        ArrayList<List<Integer>> graph = new ArrayList<>(vertices);
        for (int vertex = 0; vertex < vertices; vertex++) {
            graph.add(new ArrayList<>());
        }
        return graph;
    }

    private static void addEdge(List<List<Integer>> graph, int from, int to) {
        graph.get(from).add(to);
    }

    private static void buildSampleGraph(List<List<Integer>> graph) {
        for (int[] edge : SAMPLE_EDGES) {
            addEdge(graph, edge[0], edge[1]);
        }
    }

    private static void printOrder(List<Integer> order) {
        StringBuilder line = new StringBuilder();
        for (int vertex : order) {
            if (line.length() > 0) {
                line.append(' ');
            }
            line.append(vertex);
        }
        System.out.println(line);
    }

    public static void main(String[] args) {
        List<List<Integer>> graph = createGraph(SAMPLE_VERTEX_COUNT);
        buildSampleGraph(graph);
        printOrder(TopologicalSort.topologicalSort(graph));
    }
}
