package graph.traversal;

import graph.Graph;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * A {@link TraversalStrategy} that visits vertices depth-first using an explicit
 * stack. An explicit stack (rather than recursion) keeps traversal iterative so
 * that deep graphs cannot overflow the call stack.
 */
public final class DepthFirstTraversal implements TraversalStrategy {

    @Override
    public List<Integer> traverseFrom(Graph graph, int source, boolean[] visited) {
        List<Integer> reached = new ArrayList<>();
        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(source);

        while (!stack.isEmpty()) {
            int current = stack.pop();
            // A vertex may be stacked more than once before it is first popped.
            if (visited[current]) {
                continue;
            }
            visited[current] = true;
            reached.add(current);

            for (int neighbour : graph.neighbours(current)) {
                if (!visited[neighbour]) {
                    stack.push(neighbour);
                }
            }
        }
        return reached;
    }
}
