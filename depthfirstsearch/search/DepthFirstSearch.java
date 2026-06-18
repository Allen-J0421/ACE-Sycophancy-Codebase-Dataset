package depthfirstsearch.search;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.function.IntConsumer;

import depthfirstsearch.graph.Graph;

public final class DepthFirstSearch {

    private DepthFirstSearch() {
        // Utility class.
    }

    public static List<Integer> traverse(Graph graph) {
        Objects.requireNonNull(graph, "graph");
        List<Integer> order = new ArrayList<>(graph.vertexCount());
        traverse(graph, order::add);
        return order;
    }

    public static List<Integer> traverseFrom(Graph graph, int startVertex) {
        Objects.requireNonNull(graph, "graph");
        List<Integer> order = new ArrayList<>(graph.vertexCount());
        traverseFrom(graph, startVertex, order::add);
        return order;
    }

    public static void traverse(Graph graph, IntConsumer visitor) {
        Objects.requireNonNull(graph, "graph");
        Objects.requireNonNull(visitor, "visitor");

        int vertexCount = graph.vertexCount();
        boolean[] visited = new boolean[vertexCount];
        Deque<Integer> stack = new ArrayDeque<>();

        for (int start = 0; start < vertexCount; start++) {
            if (visited[start]) {
                continue;
            }

            traverseComponent(graph, start, visited, stack, visitor);
        }
    }

    public static void traverseFrom(Graph graph, int startVertex, IntConsumer visitor) {
        Objects.requireNonNull(graph, "graph");
        Objects.requireNonNull(visitor, "visitor");

        int vertexCount = graph.vertexCount();
        Objects.checkIndex(startVertex, vertexCount);
        boolean[] visited = new boolean[vertexCount];
        Deque<Integer> stack = new ArrayDeque<>();

        traverseComponent(graph, startVertex, visited, stack, visitor);
    }

    private static void traverseComponent(
            Graph graph,
            int startVertex,
            boolean[] visited,
            Deque<Integer> stack,
            IntConsumer visitor) {
        stack.push(startVertex);
        while (!stack.isEmpty()) {
            int vertex = stack.pop();
            if (visited[vertex]) {
                continue;
            }

            visited[vertex] = true;
            visitor.accept(vertex);

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
