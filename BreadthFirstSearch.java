import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;

final class BreadthFirstSearch {
    private BreadthFirstSearch() {}

    static <V> List<V> traverse(Graph<V> graph) {
        Objects.requireNonNull(graph, "graph must not be null");
        Set<V> visited = new HashSet<>();
        List<V> result = new ArrayList<>();

        for (V vertex : graph.vertices()) {
            if (!visited.contains(vertex)) {
                result.addAll(traverseComponent(graph, vertex, visited));
            }
        }
        return result;
    }

    private static <V> List<V> traverseComponent(Graph<V> graph, V source, Set<V> visited) {
        List<V> component = new ArrayList<>();
        Queue<V> queue = new ArrayDeque<>();
        visited.add(source);
        queue.add(source);

        while (!queue.isEmpty()) {
            V current = queue.poll();
            component.add(current);

            for (V neighbor : graph.neighbors(current)) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }
        return component;
    }
}
