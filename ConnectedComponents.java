import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public final class ConnectedComponents {

    private ConnectedComponents() {
    }

    public static List<List<Integer>> findComponents(List<? extends List<Integer>> adjacencyList) {
        return findComponents(UndirectedGraph.fromAdjacencyList(adjacencyList));
    }

    public static List<List<Integer>> findComponents(Graph graph) {
        if (graph == null) {
            throw new IllegalArgumentException("graph cannot be null");
        }

        boolean[] visited = new boolean[graph.vertexCount()];
        List<List<Integer>> components = new ArrayList<>();

        for (int vertex = 0; vertex < graph.vertexCount(); vertex++) {
            if (!visited[vertex]) {
                components.add(collectComponent(graph, vertex, visited));
            }
        }

        return components;
    }

    @Deprecated
    public static List<List<Integer>> getComponents(List<? extends List<Integer>> adjacencyList) {
        return findComponents(adjacencyList);
    }

    @Deprecated
    public static List<List<Integer>> getComponents(Graph graph) {
        return findComponents(graph);
    }

    private static List<Integer> collectComponent(Graph graph, int source, boolean[] visited) {
        Queue<Integer> queue = new ArrayDeque<>();
        List<Integer> component = new ArrayList<>();

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
