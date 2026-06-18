package depthfirstsearch.search;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.function.IntConsumer;

import depthfirstsearch.graph.Graph;

final class TraversalState {

    private final Graph graph;
    private final boolean[] visited;
    private final Deque<Integer> stack;
    private final IntConsumer visitor;

    private TraversalState(Graph graph, IntConsumer visitor) {
        this.graph = Objects.requireNonNull(graph, "graph");
        this.visitor = Objects.requireNonNull(visitor, "visitor");
        this.visited = new boolean[graph.vertexCount()];
        this.stack = new ArrayDeque<>();
    }

    static void traverseAll(Graph graph, IntConsumer visitor) {
        TraversalState state = new TraversalState(graph, visitor);
        for (int start = 0; start < state.visited.length; start++) {
            if (!state.visited[start]) {
                state.traverseFrom(start);
            }
        }
    }

    static void traverseFrom(Graph graph, int startVertex, IntConsumer visitor) {
        TraversalState state = new TraversalState(graph, visitor);
        Objects.checkIndex(startVertex, state.visited.length);
        state.traverseFrom(startVertex);
    }

    private void traverseFrom(int startVertex) {
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
