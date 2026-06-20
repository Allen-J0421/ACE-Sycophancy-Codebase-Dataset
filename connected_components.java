import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

final class ConnectedComponents {

    private ConnectedComponents() {
        // Utility class.
    }

    private static void bfs(List<List<Integer>> graph, int source, boolean[] visited, List<Integer> component) {
        Queue<Integer> queue = new ArrayDeque<>();
        visited[source] = true;
        queue.add(source);

        while (!queue.isEmpty()) {
            int currentVertex = queue.remove();
            component.add(currentVertex);

            for (int neighbor : graph.get(currentVertex)) {
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    queue.add(neighbor);
                }
            }
        }
    }

    static List<List<Integer>> getComponents(List<List<Integer>> graph) {
        int vertexCount = graph.size();
        boolean[] visited = new boolean[vertexCount];
        List<List<Integer>> components = new ArrayList<>();

        for (int vertex = 0; vertex < vertexCount; vertex++) {
            if (!visited[vertex]) {
                List<Integer> component = new ArrayList<>();
                bfs(graph, vertex, visited, component);
                components.add(component);
            }
        }
        return components;
    }

    static void addEdge(List<List<Integer>> graph, int u, int v) {
        validateVertex(graph, u);
        validateVertex(graph, v);
        graph.get(u).add(v);
        graph.get(v).add(u);
    }

    static List<List<Integer>> createGraph(int vertexCount) {
        List<List<Integer>> graph = new ArrayList<>(vertexCount);
        for (int vertex = 0; vertex < vertexCount; vertex++) {
            graph.add(new ArrayList<>());
        }
        return graph;
    }

    private static void validateVertex(List<List<Integer>> graph, int vertex) {
        if (vertex < 0 || vertex >= graph.size()) {
            throw new IllegalArgumentException("Vertex out of range: " + vertex);
        }
    }

    private static void printComponents(List<List<Integer>> components) {
        for (List<Integer> component : components) {
            for (int vertex : component) {
                System.out.print(vertex + " ");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        int vertexCount = 6;
        List<List<Integer>> graph = createGraph(vertexCount);

        addEdge(graph, 1, 2);
        addEdge(graph, 0, 3);
        addEdge(graph, 2, 0);
        addEdge(graph, 5, 4);

        List<List<Integer>> components = getComponents(graph);
        printComponents(components);
    }
}
