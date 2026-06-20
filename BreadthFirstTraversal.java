import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

final class BreadthFirstTraversal {

    private BreadthFirstTraversal() {
    }

    static ConnectedComponent traverse(
            Graph graph,
            Vertex startVertex,
            VisitedVertices visited) {
        Queue<Vertex> pendingVertices = new ArrayDeque<>();
        List<Vertex> componentVertices = new ArrayList<>();

        visited.markVisited(startVertex);
        pendingVertices.add(startVertex);

        while (!pendingVertices.isEmpty()) {
            Vertex currentVertex = pendingVertices.remove();
            componentVertices.add(currentVertex);

            for (Vertex neighbor : graph.neighborsOf(currentVertex)) {
                if (visited.markVisitedIfUnvisited(neighbor)) {
                    pendingVertices.add(neighbor);
                }
            }
        }

        return new ConnectedComponent(componentVertices);
    }
}
