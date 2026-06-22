import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

class ConnectedComponents {

    private ConnectedComponents() {}

    static List<List<Integer>> find(Graph graph) {
        int vertexCount = graph.vertexCount();
        boolean[] visited = new boolean[vertexCount];
        List<List<Integer>> components = new ArrayList<>();

        for (int vertex = 0; vertex < vertexCount; vertex++) {
            if (!visited[vertex]) {
                components.add(bfsFrom(graph, vertex, visited));
            }
        }
        return components;
    }

    private static List<Integer> bfsFrom(Graph graph, int start, boolean[] visited) {
        List<Integer> component = new ArrayList<>();
        Queue<Integer> queue = new LinkedList<>();
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
