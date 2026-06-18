import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

final class BreadthFirstSearch {
    private BreadthFirstSearch() {}

    static List<Integer> traverse(Graph graph) {
        int n = graph.vertexCount();
        boolean[] visited = new boolean[n];
        List<Integer> result = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                result.addAll(traverseComponent(graph, i, visited));
            }
        }
        return result;
    }

    private static List<Integer> traverseComponent(Graph graph, int source, boolean[] visited) {
        List<Integer> component = new ArrayList<>();
        Queue<Integer> queue = new LinkedList<>();
        visited[source] = true;
        queue.add(source);

        while (!queue.isEmpty()) {
            int current = queue.poll();
            component.add(current);

            for (int neighbor : graph.neighbors(current)) {
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    queue.add(neighbor);
                }
            }
        }
        return component;
    }
}
