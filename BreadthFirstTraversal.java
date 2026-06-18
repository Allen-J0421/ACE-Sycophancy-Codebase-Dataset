import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

final class BreadthFirstTraversal {
    private BreadthFirstTraversal() {
    }

    static List<Integer> traverse(Graph graph) {
        Objects.requireNonNull(graph, "graph must not be null");

        boolean[] visited = new boolean[graph.vertexCount()];
        List<Integer> traversal = new ArrayList<>(graph.vertexCount());

        for (int vertex = 0; vertex < graph.vertexCount(); vertex++) {
            if (!visited[vertex]) {
                traverseComponent(graph, vertex, visited, traversal);
            }
        }

        return Collections.unmodifiableList(traversal);
    }

    private static void traverseComponent(
            Graph graph,
            int source,
            boolean[] visited,
            List<Integer> traversal
    ) {
        Deque<Integer> queue = new ArrayDeque<>();
        visited[source] = true;
        queue.addLast(source);

        while (!queue.isEmpty()) {
            int current = queue.removeFirst();
            traversal.add(current);

            enqueueUnvisitedNeighbors(graph, current, visited, queue);
        }
    }

    private static void enqueueUnvisitedNeighbors(
            Graph graph,
            int current,
            boolean[] visited,
            Deque<Integer> queue
    ) {
        for (int neighbor : graph.neighborsOf(current)) {
            if (!visited[neighbor]) {
                visited[neighbor] = true;
                queue.addLast(neighbor);
            }
        }
    }
}
