import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

final class GraphTraversal {
    private final Graph graph;
    private final Set<Vertex> visited;

    private GraphTraversal(Graph graph) {
        if (graph == null) {
            throw new IllegalArgumentException("Graph must not be null.");
        }

        this.graph = graph;
        this.visited = new HashSet<>();
    }

    static GraphTraversal forGraph(Graph graph) {
        return new GraphTraversal(graph);
    }

    Deque<Vertex> buildFinishingOrder() {
        Deque<Vertex> finishingOrder = new ArrayDeque<>();

        for (Vertex vertex : graph.vertices()) {
            if (isUnvisited(vertex)) {
                traverseForFinishingOrder(vertex, finishingOrder);
            }
        }

        return finishingOrder;
    }

    List<Vertex> collectReachableVerticesFrom(Vertex startVertex) {
        validateVertex(startVertex);

        List<Vertex> visitedVertices = new ArrayList<>();
        traverseReachableVertices(startVertex, visitedVertices);
        return visitedVertices;
    }

    boolean hasVisited(Vertex vertex) {
        return visited.contains(vertex);
    }

    boolean isUnvisited(Vertex vertex) {
        return !hasVisited(vertex);
    }

    private void traverseForFinishingOrder(Vertex vertex, Deque<Vertex> finishingOrder) {
        visited.add(vertex);

        for (Vertex neighbor : graph.neighborsOf(vertex)) {
            if (isUnvisited(neighbor)) {
                traverseForFinishingOrder(neighbor, finishingOrder);
            }
        }

        finishingOrder.push(vertex);
    }

    private void traverseReachableVertices(Vertex vertex, List<Vertex> visitedVertices) {
        visited.add(vertex);
        visitedVertices.add(vertex);

        for (Vertex neighbor : graph.neighborsOf(vertex)) {
            if (isUnvisited(neighbor)) {
                traverseReachableVertices(neighbor, visitedVertices);
            }
        }
    }

    private void validateVertex(Vertex vertex) {
        if (!graph.containsVertex(vertex)) {
            throw new IllegalArgumentException("Vertex " + vertex + " is not part of the graph.");
        }
    }
}
