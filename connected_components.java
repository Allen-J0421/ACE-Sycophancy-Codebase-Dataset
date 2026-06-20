import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

final class ConnectedComponents {

    private ConnectedComponents() {
    }

    static List<List<Integer>> componentsOf(Graph graph) {
        boolean[] visited = new boolean[graph.vertexCount()];
        List<List<Integer>> components = new ArrayList<>();

        for (int vertex = 0; vertex < graph.vertexCount(); vertex++) {
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

    static final class Graph {
        private final List<List<Integer>> adjacency;

        Graph(int vertexCount) {
            if (vertexCount < 0) {
                throw new IllegalArgumentException("vertexCount must be non-negative");
            }
            adjacency = new ArrayList<>(vertexCount);
            for (int vertex = 0; vertex < vertexCount; vertex++) {
                adjacency.add(new ArrayList<>());
            }
        }

        int vertexCount() {
            return adjacency.size();
        }

        void addEdge(int u, int v) {
            validateVertex(u);
            validateVertex(v);
            adjacency.get(u).add(v);
            adjacency.get(v).add(u);
        }

        List<Integer> neighborsOf(int vertex) {
            validateVertex(vertex);
            return adjacency.get(vertex);
        }

        private void validateVertex(int vertex) {
            if (vertex < 0 || vertex >= adjacency.size()) {
                throw new IllegalArgumentException("Vertex out of range: " + vertex);
            }
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
        Graph graph = new Graph(6);

        graph.addEdge(1, 2);
        graph.addEdge(0, 3);
        graph.addEdge(2, 0);
        graph.addEdge(5, 4);

        List<List<Integer>> components = componentsOf(graph);
        printComponents(components);
    }
}
