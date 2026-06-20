import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

final class ConnectedComponents {

    private ConnectedComponents() {
    }

    static List<List<Integer>> findComponents(UndirectedGraph graph) {
        int vertexCount = graph.size();
        boolean[] visited = new boolean[vertexCount];
        List<List<Integer>> components = new ArrayList<>(vertexCount);

        for (int vertex = 0; vertex < vertexCount; vertex++) {
            if (!visited[vertex]) {
                List<Integer> component = new ArrayList<>();
                bfs(graph, vertex, visited, component);
                components.add(Collections.unmodifiableList(component));
            }
        }
        return Collections.unmodifiableList(components);
    }

    private static void bfs(UndirectedGraph graph, int source, boolean[] visited, List<Integer> component) {
        Deque<Integer> queue = new ArrayDeque<>();
        visited[source] = true;
        queue.addLast(source);

        while (!queue.isEmpty()) {
            int currentVertex = queue.removeFirst();
            component.add(currentVertex);

            for (int neighbor : graph.neighborsOf(currentVertex)) {
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    queue.addLast(neighbor);
                }
            }
        }
    }
}
