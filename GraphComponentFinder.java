import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

final class GraphComponentFinder {

    private GraphComponentFinder() {
    }

    static List<List<Integer>> findConnectedComponents(UndirectedGraph graph) {
        boolean[] visited = new boolean[graph.vertexCount()];
        List<List<Integer>> components = new ArrayList<>();

        for (int vertex = 0; vertex < graph.vertexCount(); vertex++) {
            if (!visited[vertex]) {
                components.add(traverseComponent(graph, vertex, visited));
            }
        }

        return components;
    }

    private static List<Integer> traverseComponent(
            UndirectedGraph graph,
            int startVertex,
            boolean[] visited) {
        Queue<Integer> pendingVertices = new ArrayDeque<>();
        List<Integer> component = new ArrayList<>();

        visited[startVertex] = true;
        pendingVertices.add(startVertex);

        while (!pendingVertices.isEmpty()) {
            int currentVertex = pendingVertices.remove();
            component.add(currentVertex);

            for (int neighbor : graph.neighborsOf(currentVertex)) {
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    pendingVertices.add(neighbor);
                }
            }
        }

        return component;
    }
}
