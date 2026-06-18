import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

public final class BreadthFirstSearch {

    private BreadthFirstSearch() {
        // Utility class.
    }

    private static void bfsComponent(Graph graph, int source, boolean[] visited, List<Integer> result) {
        Deque<Integer> queue = new ArrayDeque<>();
        visited[source] = true;
        queue.addLast(source);

        while (!queue.isEmpty()) {
            int current = queue.removeFirst();
            result.add(current);

            for (int neighbor : graph.neighbors(current)) {
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    queue.addLast(neighbor);
                }
            }
        }
    }

    public static List<Integer> bfs(Graph graph) {
        Objects.requireNonNull(graph, "graph");

        int vertexCount = graph.vertexCount();
        boolean[] visited = new boolean[vertexCount];
        List<Integer> result = new ArrayList<>(vertexCount);

        for (int vertex = 0; vertex < vertexCount; vertex++) {
            if (!visited[vertex]) {
                bfsComponent(graph, vertex, visited, result);
            }
        }
        return result;
    }

    public static List<Integer> bfsFromSource(Graph graph, int source) {
        Objects.requireNonNull(graph, "graph");
        graph.validateVertex(source);

        boolean[] visited = new boolean[graph.vertexCount()];
        List<Integer> result = new ArrayList<>();
        bfsComponent(graph, source, visited, result);
        return result;
    }
}
