import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

class ConnectedComponents {

    static List<List<Integer>> getComponents(List<List<Integer>> graph) {
        validateGraph(graph);

        boolean[] visited = new boolean[graph.size()];
        List<List<Integer>> components = new ArrayList<>();

        for (int vertex = 0; vertex < graph.size(); vertex++) {
            if (!visited[vertex]) {
                List<Integer> component = new ArrayList<>();
                collectComponent(graph, vertex, visited, component);
                components.add(component);
            }
        }

        return components;
    }

    static void addEdge(List<List<Integer>> graph, int firstVertex, int secondVertex) {
        validateVertex(graph, firstVertex);
        validateVertex(graph, secondVertex);

        graph.get(firstVertex).add(secondVertex);
        graph.get(secondVertex).add(firstVertex);
    }

    private static void collectComponent(
            List<List<Integer>> graph,
            int source,
            boolean[] visited,
            List<Integer> component
    ) {
        Queue<Integer> queue = new ArrayDeque<>();
        visited[source] = true;
        queue.add(source);

        while (!queue.isEmpty()) {
            int current = queue.poll();
            component.add(current);

            for (int neighbor : graph.get(current)) {
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    queue.add(neighbor);
                }
            }
        }
    }

    private static List<List<Integer>> createGraph(int vertexCount) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("vertexCount must be non-negative");
        }

        List<List<Integer>> graph = new ArrayList<>(vertexCount);
        for (int vertex = 0; vertex < vertexCount; vertex++) {
            graph.add(new ArrayList<>());
        }
        return graph;
    }

    private static void validateGraph(List<List<Integer>> graph) {
        if (graph == null) {
            throw new IllegalArgumentException("graph cannot be null");
        }

        for (int vertex = 0; vertex < graph.size(); vertex++) {
            List<Integer> neighbors = graph.get(vertex);
            if (neighbors == null) {
                throw new IllegalArgumentException("adjacency list cannot be null for vertex " + vertex);
            }

            for (int neighbor : neighbors) {
                validateVertex(graph, neighbor);
            }
        }
    }

    private static void validateVertex(List<List<Integer>> graph, int vertex) {
        if (graph == null) {
            throw new IllegalArgumentException("graph cannot be null");
        }
        if (vertex < 0 || vertex >= graph.size()) {
            throw new IllegalArgumentException("vertex out of range: " + vertex);
        }
    }

    private static void printComponents(List<List<Integer>> components) {
        for (List<Integer> component : components) {
            StringBuilder line = new StringBuilder();
            for (int vertex : component) {
                if (line.length() > 0) {
                    line.append(' ');
                }
                line.append(vertex);
            }
            System.out.println(line);
        }
    }

    public static void main(String[] args) {
        List<List<Integer>> graph = createGraph(6);

        addEdge(graph, 1, 2);
        addEdge(graph, 0, 3);
        addEdge(graph, 2, 0);
        addEdge(graph, 5, 4);

        printComponents(getComponents(graph));
    }
}
