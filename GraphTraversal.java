import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

final class GraphTraversal {
    private final Graph graph;
    private final boolean[] visited;

    private GraphTraversal(Graph graph) {
        if (graph == null) {
            throw new IllegalArgumentException("Graph must not be null.");
        }

        this.graph = graph;
        this.visited = new boolean[graph.vertexCount()];
    }

    static GraphTraversal forGraph(Graph graph) {
        return new GraphTraversal(graph);
    }

    Deque<Integer> buildFinishingOrder() {
        Deque<Integer> finishingOrder = new ArrayDeque<>();

        for (int vertex = 0; vertex < graph.vertexCount(); vertex++) {
            if (isUnvisited(vertex)) {
                traverseForFinishingOrder(vertex, finishingOrder);
            }
        }

        return finishingOrder;
    }

    List<Integer> collectReachableVerticesFrom(int startVertex) {
        List<Integer> visitedVertices = new ArrayList<>();
        traverseReachableVertices(startVertex, visitedVertices);
        return visitedVertices;
    }

    boolean hasVisited(int vertex) {
        return visited[vertex];
    }

    boolean isUnvisited(int vertex) {
        return !hasVisited(vertex);
    }

    private void traverseForFinishingOrder(int vertex, Deque<Integer> finishingOrder) {
        visited[vertex] = true;

        for (int neighbor : graph.neighborsOf(vertex)) {
            if (isUnvisited(neighbor)) {
                traverseForFinishingOrder(neighbor, finishingOrder);
            }
        }

        finishingOrder.push(vertex);
    }

    private void traverseReachableVertices(int vertex, List<Integer> visitedVertices) {
        visited[vertex] = true;
        visitedVertices.add(vertex);

        for (int neighbor : graph.neighborsOf(vertex)) {
            if (isUnvisited(neighbor)) {
                traverseReachableVertices(neighbor, visitedVertices);
            }
        }
    }
}
