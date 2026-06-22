package maxflow;

import java.util.Arrays;
import java.util.Optional;

/**
 * Finds an augmenting path using iterative depth-first search. Combining this
 * strategy with {@link FordFulkersonSolver} gives the classic Ford-Fulkerson
 * method. It still computes a correct maximum flow for integer capacities, but,
 * unlike {@link BreadthFirstPathFinder}, the number of iterations can depend on
 * the magnitude of the capacities.
 */
public final class DepthFirstPathFinder implements AugmentingPathFinder {

    @Override
    public Optional<AugmentingPath> findPath(ResidualGraph residual, int source, int sink) {
        int n = residual.vertexCount();
        boolean[] visited = new boolean[n];
        int[] parent = new int[n];
        Arrays.fill(parent, -1);

        int[] stack = new int[n];
        int top = 0;
        stack[top++] = source;
        visited[source] = true;

        while (top > 0) {
            int u = stack[--top];
            if (u == sink) {
                return Optional.of(AugmentingPath.fromParents(parent, source, sink, residual));
            }
            for (int v = 0; v < n; v++) {
                if (!visited[v] && residual.hasResidualCapacity(u, v)) {
                    visited[v] = true;
                    parent[v] = u;
                    stack[top++] = v;
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public String name() {
        return "depth-first (Ford-Fulkerson)";
    }
}
