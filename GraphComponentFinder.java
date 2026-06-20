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

        for (Vertex vertex : graph.vertices()) {
            if (!visited[vertex.index()]) {
                components.add(traverseComponent(graph, vertex, vertexCount, visited));
            }
        }

        return new ConnectedComponentsResult(components);
    }

    private static ConnectedComponent traverseComponent(
            Graph graph,
            Vertex startVertex,
            int vertexCount,
            boolean[] visited) {
        Queue<Vertex> pendingVertices = new ArrayDeque<>();
        List<Vertex> component = new ArrayList<>();

        visited[startVertex.index()] = true;
        pendingVertices.add(startVertex);

        while (!pendingVertices.isEmpty()) {
            Vertex currentVertex = pendingVertices.remove();
            component.add(currentVertex);

            for (Vertex neighbor : graph.neighborsOf(currentVertex)) {
                validateNeighborVertex(neighbor, vertexCount);
                if (!visited[neighbor.index()]) {
                    visited[neighbor.index()] = true;
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

    private static void validateNeighborVertex(Vertex vertex, int vertexCount) {
        if (vertex == null) {
            throw new IllegalArgumentException("Neighbor vertex must not be null.");
        }
        if (vertex.index() < 0 || vertex.index() >= vertexCount) {
            throw new IllegalArgumentException("Neighbor index out of bounds: " + vertex.index());
        }
    }
}
