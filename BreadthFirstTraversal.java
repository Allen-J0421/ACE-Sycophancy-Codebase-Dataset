import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

final class BreadthFirstTraversal {
    private BreadthFirstTraversal() {
    }

    static List<Integer> traverseFrom(Graph graph, int startVertex) {
        validateGraph(graph);
        validateStartVertex(graph, startVertex);

        boolean[] visited = new boolean[graph.vertexCount()];
        List<Integer> traversalOrder = new ArrayList<>();
        traverseComponent(graph, startVertex, visited, traversalOrder);
        return traversalOrder;
    }

    static List<Integer> traverseAllComponents(Graph graph) {
        validateGraph(graph);

        boolean[] visited = new boolean[graph.vertexCount()];
        List<Integer> traversalOrder = new ArrayList<>();

        for (int vertex = 0; vertex < graph.vertexCount(); vertex++) {
            if (!visited[vertex]) {
                traverseComponent(graph, vertex, visited, traversalOrder);
            }
        }

        return traversalOrder;
    }

    private static void traverseComponent(
        Graph graph,
        int startVertex,
        boolean[] visited,
        List<Integer> traversalOrder
    ) {
        Deque<Integer> queue = new ArrayDeque<>();
        visited[startVertex] = true;
        queue.add(startVertex);

        while (!queue.isEmpty()) {
            int currentVertex = queue.remove();
            traversalOrder.add(currentVertex);

            for (int neighbor : graph.neighborsOf(currentVertex)) {
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    queue.add(neighbor);
                }
            }
        }
    }

    private static void validateGraph(Graph graph) {
        if (graph == null) {
            throw new IllegalArgumentException("Graph cannot be null.");
        }
    }

    private static void validateStartVertex(Graph graph, int startVertex) {
        if (startVertex < 0 || startVertex >= graph.vertexCount()) {
            throw new IllegalArgumentException("Start vertex out of range: " + startVertex);
        }
    }
}
