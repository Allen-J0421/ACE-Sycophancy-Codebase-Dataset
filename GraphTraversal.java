import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

final class GraphTraversal {
    private final Graph graph;
    private final Set<Integer> visited;

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

    Deque<Integer> buildFinishingOrder() {
        Deque<Integer> finishingOrder = new ArrayDeque<>();

        for (int vertex : graph.vertices()) {
            if (isUnvisited(vertex)) {
                traverseForFinishingOrder(vertex, finishingOrder);
            }
        }

        return finishingOrder;
    }

    List<Integer> collectReachableVerticesFrom(int startVertex) {
        validateVertex(startVertex);

        List<Integer> visitedVertices = new ArrayList<>();
        traverseReachableVertices(startVertex, visitedVertices);
        return visitedVertices;
    }

    boolean hasVisited(int vertex) {
        return visited.contains(vertex);
    }

    boolean isUnvisited(int vertex) {
        return !hasVisited(vertex);
    }

    private void traverseForFinishingOrder(int vertex, Deque<Integer> finishingOrder) {
        visited.add(vertex);

        for (int neighbor : graph.neighborsOf(vertex)) {
            if (isUnvisited(neighbor)) {
                traverseForFinishingOrder(neighbor, finishingOrder);
            }
        }

        finishingOrder.push(vertex);
    }

    private void traverseReachableVertices(int vertex, List<Integer> visitedVertices) {
        visited.add(vertex);
        visitedVertices.add(vertex);

        for (int neighbor : graph.neighborsOf(vertex)) {
            if (isUnvisited(neighbor)) {
                traverseReachableVertices(neighbor, visitedVertices);
            }
        }
    }

    private void validateVertex(int vertex) {
        if (!graph.containsVertex(vertex)) {
            throw new IllegalArgumentException("Vertex " + vertex + " is not part of the graph.");
        }
    }
}
