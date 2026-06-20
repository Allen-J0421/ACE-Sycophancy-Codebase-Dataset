import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Queue;

final class GraphComponentFinder {

    private GraphComponentFinder() {
    }

    static List<List<Integer>> findConnectedComponents(Graph graph) {
        Objects.requireNonNull(graph, "graph");

        int vertexCount = graph.vertexCount();
        validateVertexCount(vertexCount);
        boolean[] visited = new boolean[vertexCount];
        List<List<Integer>> components = new ArrayList<>();

        for (int vertex = 0; vertex < vertexCount; vertex++) {
            if (!visited[vertex]) {
                components.add(traverseComponent(graph, vertex, visited));
            }
        }

        return Collections.unmodifiableList(components);
    }

    private static List<Integer> traverseComponent(
            Graph graph,
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
                validateNeighborVertex(neighbor, graph.vertexCount());
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    pendingVertices.add(neighbor);
                }
            }
        }

        return Collections.unmodifiableList(component);
    }

    private static void validateVertexCount(int vertexCount) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("Vertex count must be non-negative.");
        }
    }

    private static void validateNeighborVertex(int vertex, int vertexCount) {
        if (vertex < 0 || vertex >= vertexCount) {
            throw new IllegalArgumentException("Neighbor index out of bounds: " + vertex);
        }
    }
}
