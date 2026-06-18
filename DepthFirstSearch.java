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

        boolean[] visited = new boolean[graph.size()];
        List<Integer> order = new ArrayList<>(graph.size());
        Deque<Integer> stack = new ArrayDeque<>();

        for (int start = 0; start < graph.size(); start++) {
            if (visited[start]) {
                continue;
            }

            stack.push(start);
            while (!stack.isEmpty()) {
                int vertex = stack.pop();
                if (visited[vertex]) {
                    continue;
                }

                visited[vertex] = true;
                order.add(vertex);

                List<Integer> neighbors = graph.neighborsOf(vertex);
                for (int index = neighbors.size() - 1; index >= 0; index--) {
                    int neighbor = neighbors.get(index);
                    if (!visited[neighbor]) {
                        stack.push(neighbor);
                    }
                }
            }
        }

        return order;
    }
}
