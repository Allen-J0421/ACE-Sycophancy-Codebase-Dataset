import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

final class GraphComponentFinder {

    private GraphComponentFinder() {
    }

    static ConnectedComponentsResult findConnectedComponents(Graph graph) {
        GraphValidator.validate(graph);

        VisitedVertices visited = new VisitedVertices(graph.vertexCount());
        List<ConnectedComponent> components = new ArrayList<>();

        for (Vertex vertex : graph.vertices()) {
            if (!visited.isVisited(vertex)) {
                components.add(traverseComponent(graph, vertex, visited));
            }
        }

        return new ConnectedComponentsResult(components);
    }

    private static ConnectedComponent traverseComponent(
            Graph graph,
            Vertex startVertex,
            VisitedVertices visited) {
        Queue<Vertex> pendingVertices = new ArrayDeque<>();
        List<Vertex> component = new ArrayList<>();

        visited.markVisited(startVertex);
        pendingVertices.add(startVertex);

        while (!pendingVertices.isEmpty()) {
            Vertex currentVertex = pendingVertices.remove();
            component.add(currentVertex);

            for (Vertex neighbor : graph.neighborsOf(currentVertex)) {
                if (!visited.isVisited(neighbor)) {
                    visited.markVisited(neighbor);
                    pendingVertices.add(neighbor);
                }
            }
        }

        return new ConnectedComponent(component);
    }
}
