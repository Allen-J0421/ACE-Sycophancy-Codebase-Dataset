import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

final class DepthFirstTraversal {

    Deque<Integer> buildFinishingOrder(DirectedGraph graph) {
        boolean[] visited = new boolean[graph.vertexCount()];
        Deque<Integer> finishingOrder = new ArrayDeque<>();

        for (int vertex = 0; vertex < graph.vertexCount(); vertex++) {
            if (!visited[vertex]) {
                traverseForFinishingOrder(vertex, graph, visited, finishingOrder);
            }
        }

        return finishingOrder;
    }

    List<Integer> collectReachableVertices(int startVertex, DirectedGraph graph, boolean[] visited) {
        List<Integer> visitedVertices = new ArrayList<>();
        traverseReachableVertices(startVertex, graph, visited, visitedVertices);
        return visitedVertices;
    }

    private void traverseForFinishingOrder(
        int vertex,
        DirectedGraph graph,
        boolean[] visited,
        Deque<Integer> finishingOrder
    ) {
        visited[vertex] = true;

        for (int neighbor : graph.neighborsOf(vertex)) {
            if (!visited[neighbor]) {
                traverseForFinishingOrder(neighbor, graph, visited, finishingOrder);
            }
        }

        finishingOrder.push(vertex);
    }

    private void traverseReachableVertices(
        int vertex,
        DirectedGraph graph,
        boolean[] visited,
        List<Integer> visitedVertices
    ) {
        visited[vertex] = true;
        visitedVertices.add(vertex);

        for (int neighbor : graph.neighborsOf(vertex)) {
            if (!visited[neighbor]) {
                traverseReachableVertices(neighbor, graph, visited, visitedVertices);
            }
        }
    }
}
