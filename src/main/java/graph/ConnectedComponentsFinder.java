package graph;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Finds the {@link Components} of an undirected {@link Graph} using
 * breadth-first search.
 */
public final class ConnectedComponentsFinder {

    /** Returns the connected components of {@code graph}. */
    public Components find(Graph graph) {
        boolean[] visited = new boolean[graph.vertexCount()];
        List<List<Integer>> components = new ArrayList<>();

        for (int vertex = 0; vertex < graph.vertexCount(); vertex++) {
            if (!visited[vertex]) {
                components.add(exploreFrom(graph, vertex, visited));
            }
        }
        return new Components(components, graph.vertexCount());
    }

    /** Collects every vertex reachable from {@code source}, marking it visited. */
    private List<Integer> exploreFrom(Graph graph, int source, boolean[] visited) {
        List<Integer> component = new ArrayList<>();
        Deque<Integer> queue = new ArrayDeque<>();

        visited[source] = true;
        queue.add(source);

        while (!queue.isEmpty()) {
            int current = queue.poll();
            component.add(current);

            for (int neighbour : graph.neighbours(current)) {
                if (!visited[neighbour]) {
                    visited[neighbour] = true;
                    queue.add(neighbour);
                }
            }
        }
        return component;
    }
}
