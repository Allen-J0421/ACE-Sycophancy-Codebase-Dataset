import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.IntConsumer;

public final class DepthFirstSearch {

    private DepthFirstSearch() {
    }

    public static List<Integer> dfs(IntGraph graph) {
        return toList(dfsToArray(graph));
    }

    public static List<Integer> dfsFrom(IntGraph graph, int startVertex) {
        return toList(dfsFromToArray(graph, startVertex));
    }

    public static int[] dfsToArray(IntGraph graph) {
        return new TraversalSession(graph).traverseAllComponents();
    }

    public static int[] dfsFromToArray(IntGraph graph, int startVertex) {
        return new TraversalSession(graph).traverseFrom(startVertex);
    }

    public static void walk(IntGraph graph, IntConsumer visitor) {
        Objects.requireNonNull(visitor, "visitor");
        int[] traversal = dfsToArray(graph);
        for (int vertex : traversal) {
            visitor.accept(vertex);
        }
    }

    public static void walkFrom(IntGraph graph, int startVertex, IntConsumer visitor) {
        Objects.requireNonNull(visitor, "visitor");
        int[] traversal = dfsFromToArray(graph, startVertex);
        for (int vertex : traversal) {
            visitor.accept(vertex);
        }
    }

    private static List<Integer> toList(int[] traversal) {
        List<Integer> vertices = new ArrayList<>(traversal.length);
        for (int vertex : traversal) {
            vertices.add(vertex);
        }
        return vertices;
    }

    private static final class TraversalSession {
        private final IntGraph graph;
        private final boolean[] visited;
        private final IntStack stack;
        private final IntCollector traversal;

        private TraversalSession(IntGraph graph) {
            this.graph = Objects.requireNonNull(graph, "graph");
            this.visited = new boolean[graph.vertexCount()];
            this.stack = new IntStack(graph.vertexCount());
            this.traversal = new IntCollector(graph.vertexCount());
        }

        private int[] traverseAllComponents() {
            for (int vertex = 0; vertex < graph.vertexCount(); vertex++) {
                if (!visited[vertex]) {
                    traverseComponent(vertex);
                }
            }

            return traversal.toArray();
        }

        private int[] traverseFrom(int startVertex) {
            validateVertex(startVertex);
            traverseComponent(startVertex);
            return traversal.toArray();
        }

        private void traverseComponent(int startVertex) {
            stack.push(startVertex);

            while (!stack.isEmpty()) {
                int vertex = stack.pop();
                if (visited[vertex]) {
                    continue;
                }

                visited[vertex] = true;
                traversal.add(vertex);

                for (int index = graph.neighborCount(vertex) - 1; index >= 0; index--) {
                    int neighbor = graph.neighborAt(vertex, index);
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

    private static final class IntStack {
        private int[] values;
        private int size;

        private IntStack(int initialCapacity) {
            this.values = new int[Math.max(1, initialCapacity)];
        }

        private void push(int value) {
            ensureCapacity(size + 1);
            values[size] = value;
            size++;
        }

        private int pop() {
            size--;
            return values[size];
        }

        private boolean isEmpty() {
            return size == 0;
        }

        private void ensureCapacity(int minCapacity) {
            if (minCapacity <= values.length) {
                return;
            }

            int[] expanded = new int[Math.max(minCapacity, values.length * 2)];
            System.arraycopy(values, 0, expanded, 0, size);
            values = expanded;
        }
    }

    private static final class IntCollector {
        private int[] values;
        private int size;

        private IntCollector(int initialCapacity) {
            this.values = new int[Math.max(1, initialCapacity)];
        }

        private void add(int value) {
            ensureCapacity(size + 1);
            values[size] = value;
            size++;
        }

        private int[] toArray() {
            int[] copy = new int[size];
            System.arraycopy(values, 0, copy, 0, size);
            return copy;
        }

        private void ensureCapacity(int minCapacity) {
            if (minCapacity <= values.length) {
                return;
            }

            int[] expanded = new int[Math.max(minCapacity, values.length * 2)];
            System.arraycopy(values, 0, expanded, 0, size);
            values = expanded;
        }
    }
}
