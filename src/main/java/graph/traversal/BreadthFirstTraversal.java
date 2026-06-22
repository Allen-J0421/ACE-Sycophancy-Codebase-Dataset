package graph.traversal;

import graph.Graph;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/** A {@link TraversalStrategy} that visits vertices breadth-first using a queue. */
public final class BreadthFirstTraversal implements TraversalStrategy {

    @Override
    public List<Integer> traverseFrom(Graph graph, int source, boolean[] visited) {
        List<Integer> reached = new ArrayList<>();
        Deque<Integer> queue = new ArrayDeque<>();

        visited[source] = true;
        queue.add(source);

        while (!queue.isEmpty()) {
            int current = queue.poll();
            reached.add(current);

            for (int neighbour : graph.neighbours(current)) {
                if (!visited[neighbour]) {
                    visited[neighbour] = true;
                    queue.add(neighbour);
                }
            }
        }
        return reached;
    }
}
