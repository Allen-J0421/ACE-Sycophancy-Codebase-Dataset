import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class DepthFirstSearch implements GraphTraversal {

    private enum TraversalMode {
        RECURSIVE, ITERATIVE
    }

    private final TraversalMode mode;

    public DepthFirstSearch() {
        this(TraversalMode.RECURSIVE);
    }

    public DepthFirstSearch(boolean useIterative) {
        this(useIterative ? TraversalMode.ITERATIVE : TraversalMode.RECURSIVE);
    }

    private DepthFirstSearch(TraversalMode mode) {
        this.mode = mode;
    }

    public static DepthFirstSearch recursive() {
        return new DepthFirstSearch(TraversalMode.RECURSIVE);
    }

    public static DepthFirstSearch iterative() {
        return new DepthFirstSearch(TraversalMode.ITERATIVE);
    }

    @Override
    public List<Integer> traverse(Graph graph) {
        return mode == TraversalMode.RECURSIVE
                ? traverseRecursive(graph)
                : traverseIterative(graph);
    }

    private List<Integer> traverseRecursive(Graph graph) {
        boolean[] visited = new boolean[graph.getVertexCount()];
        List<Integer> result = new ArrayList<>();

        for (int i = 0; i < graph.getVertexCount(); i++) {
            if (!visited[i]) {
                dfsRecursive(graph, i, visited, result);
            }
        }

        return result;
    }

    private void dfsRecursive(Graph graph, int vertex, boolean[] visited, List<Integer> result) {
        visited[vertex] = true;
        result.add(vertex);

        for (int neighbor : graph.getNeighbors(vertex)) {
            if (!visited[neighbor]) {
                dfsRecursive(graph, neighbor, visited, result);
            }
        }
    }

    private List<Integer> traverseIterative(Graph graph) {
        boolean[] visited = new boolean[graph.getVertexCount()];
        List<Integer> result = new ArrayList<>();
        Deque<Integer> stack = new ArrayDeque<>();

        for (int i = 0; i < graph.getVertexCount(); i++) {
            if (!visited[i]) {
                dfsIterative(graph, i, visited, result, stack);
            }
        }

        return result;
    }

    private void dfsIterative(Graph graph, int start, boolean[] visited, List<Integer> result, Deque<Integer> stack) {
        stack.push(start);

        while (!stack.isEmpty()) {
            int vertex = stack.pop();

            if (!visited[vertex]) {
                visited[vertex] = true;
                result.add(vertex);

                List<Integer> neighbors = new ArrayList<>(graph.getNeighbors(vertex));
                for (int i = neighbors.size() - 1; i >= 0; i--) {
                    int neighbor = neighbors.get(i);
                    if (!visited[neighbor]) {
                        stack.push(neighbor);
                    }
                }
            }
        }
    }
}
