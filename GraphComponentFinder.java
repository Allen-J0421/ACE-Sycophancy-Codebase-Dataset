import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;

final class GraphComponentFinder {

    private GraphComponentFinder() {
    }

    static ConnectedComponentsResult findConnectedComponents(Graph graph) {
        Objects.requireNonNull(graph, "graph");

        int vertexCount = graph.vertexCount();
        validateVertexCount(vertexCount);
        boolean[] visited = new boolean[vertexCount];
        List<ConnectedComponent> components = new ArrayList<>();

        for (int vertex = 0; vertex < vertexCount; vertex++) {
            if (!visited[vertex]) {
                components.add(traverseComponent(graph, vertex, vertexCount, visited));
            }
        }

        return new ConnectedComponentsResult(components);
    }

    private static ConnectedComponent traverseComponent(
            Graph graph,
            int startVertex,
            int vertexCount,
            boolean[] visited) {
        Queue<Integer> pendingVertices = new ArrayDeque<>();
        List<Integer> component = new ArrayList<>();

        visited[startVertex] = true;
        pendingVertices.add(startVertex);

        while (!pendingVertices.isEmpty()) {
            int currentVertex = pendingVertices.remove();
            component.add(currentVertex);

            for (int neighbor : graph.neighborsOf(currentVertex)) {
                validateNeighborVertex(neighbor, vertexCount);
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    pendingVertices.add(neighbor);
                }
            }
        }

        return new ConnectedComponent(component);
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
