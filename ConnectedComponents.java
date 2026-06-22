import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

class ConnectedComponents {

    private ConnectedComponents() {}

    static List<List<Integer>> find(Graph graph) {
        if (graph == null) {
            throw new IllegalArgumentException("graph must not be null");
        }
        int vertexCount = graph.vertexCount();
        boolean[] visited = new boolean[vertexCount];
        List<List<Integer>> components = new ArrayList<>();

        for (int vertex = 0; vertex < vertexCount; vertex++) {
            if (!visited[vertex]) {
                components.add(bfsFrom(graph, vertex, visited));
            }
        }
        return components.stream()
                .map(Collections::unmodifiableList)
                .collect(Collectors.toUnmodifiableList());
    }

    private static List<Integer> bfsFrom(Graph graph, int start, boolean[] visited) {
        List<Integer> component = new ArrayList<>();
        Deque<Integer> queue = new ArrayDeque<>();
        visited[start] = true;
        queue.add(start);

        while (!queue.isEmpty()) {
            int current = queue.poll();
            component.add(current);

            for (int neighbor : graph.neighborsOf(current)) {
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    queue.add(neighbor);
                }
            }
        }
        return component;
    }

}
