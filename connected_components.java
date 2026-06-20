import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

final class ConnectedComponents {

    private ConnectedComponents() {
    }

    static List<List<Integer>> findComponents(Graph graph) {
        boolean[] visited = new boolean[graph.size()];
        List<List<Integer>> components = new ArrayList<>();

        for (int vertex = 0; vertex < graph.size(); vertex++) {
            if (!visited[vertex]) {
                List<Integer> component = new ArrayList<>();
                bfs(graph, vertex, visited, component);
                components.add(component);
            }
        }
        return components;
    }

    private static void bfs(Graph graph, int source, boolean[] visited, List<Integer> component) {
        Queue<Integer> queue = new ArrayDeque<>();
        visited[source] = true;
        queue.add(source);

        while (!queue.isEmpty()) {
            int currentVertex = queue.remove();
            component.add(currentVertex);

            for (int neighbor : graph.neighborsOf(currentVertex)) {
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    queue.add(neighbor);
                }
            }
        }
    }
}
