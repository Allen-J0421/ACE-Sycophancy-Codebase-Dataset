import java.util.ArrayList;
import java.util.List;

final class GraphComponentFinder {

    private GraphComponentFinder() {
    }

    static ConnectedComponentsResult findConnectedComponents(Graph graph) {
        GraphValidator.validate(graph);

        VisitedVertices visited = new VisitedVertices(graph.vertexCount());
        List<ConnectedComponent> components = new ArrayList<>();

        for (Vertex vertex : graph.vertices()) {
            if (!visited.isVisited(vertex)) {
                components.add(BreadthFirstTraversal.traverse(graph, vertex, visited));
            }
        }

        return new ConnectedComponentsResult(components);
    }
}
