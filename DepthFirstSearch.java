import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.function.IntConsumer;

public final class DepthFirstSearch {

    private DepthFirstSearch() {
    }

    public static List<Integer> dfs(IntGraph graph) {
        List<Integer> traversal = new ArrayList<>();
        walk(graph, traversal::add);
        return traversal;
    }

    public static List<Integer> dfsFrom(IntGraph graph, int startVertex) {
        List<Integer> traversal = new ArrayList<>();
        walkFrom(graph, startVertex, traversal::add);
        return traversal;
    }

    public static void walk(IntGraph graph, IntConsumer visitor) {
        new TraversalSession(graph, visitor).traverseAllComponents();
    }

    public static void walkFrom(IntGraph graph, int startVertex, IntConsumer visitor) {
        new TraversalSession(graph, visitor).traverseFrom(startVertex);
    }

    private static final class TraversalSession {
        private final IntGraph graph;
        private final IntConsumer visitor;
        private final boolean[] visited;

        private TraversalSession(IntGraph graph, IntConsumer visitor) {
            this.graph = Objects.requireNonNull(graph, "graph");
            this.visitor = Objects.requireNonNull(visitor, "visitor");
            this.visited = new boolean[graph.vertexCount()];
        }

        private void traverseAllComponents() {
            for (int vertex = 0; vertex < graph.vertexCount(); vertex++) {
                if (!visited[vertex]) {
                    traverseComponent(vertex);
                }
            }
        }

        private void traverseFrom(int startVertex) {
            validateVertex(startVertex);
            traverseComponent(startVertex);
        }

        private void traverseComponent(int startVertex) {
            Deque<Integer> stack = new ArrayDeque<>();
            stack.push(startVertex);

            while (!stack.isEmpty()) {
                int vertex = stack.pop();
                if (visited[vertex]) {
                    continue;
                }

                visited[vertex] = true;
                visitor.accept(vertex);

                List<Integer> neighbors = graph.neighborsOf(vertex);
                for (int index = neighbors.size() - 1; index >= 0; index--) {
                    int neighbor = neighbors.get(index);
                    if (!visited[neighbor]) {
                        stack.push(neighbor);
                    }
                }
            }
        }

        private void validateVertex(int vertex) {
            if (vertex < 0 || vertex >= visited.length) {
                throw new IllegalArgumentException("vertex out of bounds: " + vertex);
            }
        }
    }
}
