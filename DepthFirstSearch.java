import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

public final class DepthFirstSearch {

    private DepthFirstSearch() {
        // Utility class.
    }

    public static List<Integer> traverse(Graph graph) {
        Objects.requireNonNull(graph, "graph");

        int vertexCount = graph.vertexCount();
        boolean[] visited = new boolean[vertexCount];
        List<Integer> order = new ArrayList<>(vertexCount);
        Deque<Integer> stack = new ArrayDeque<>();

        for (int start = 0; start < vertexCount; start++) {
            if (visited[start]) {
                continue;
            }

            traverseComponent(graph, start, visited, stack, order);
        }

        return order;
    }

    public static List<Integer> traverseFrom(Graph graph, int startVertex) {
        Objects.requireNonNull(graph, "graph");

        int vertexCount = graph.vertexCount();
        Objects.checkIndex(startVertex, vertexCount);
        boolean[] visited = new boolean[vertexCount];
        List<Integer> order = new ArrayList<>(vertexCount);
        Deque<Integer> stack = new ArrayDeque<>();

        traverseComponent(graph, startVertex, visited, stack, order);
        return order;
    }

    private static void traverseComponent(
            Graph graph,
            int startVertex,
            boolean[] visited,
            Deque<Integer> stack,
            List<Integer> order) {
        stack.push(startVertex);
        while (!stack.isEmpty()) {
            int vertex = stack.pop();
            if (visited[vertex]) {
                continue;
            }

            visited[vertex] = true;
            order.add(vertex);

            List<Integer> neighbors = graph.neighbors(vertex);
            for (int index = neighbors.size() - 1; index >= 0; index--) {
                int neighbor = neighbors.get(index);
                if (!visited[neighbor]) {
                    stack.push(neighbor);
                }
            }
        }
    }
}
