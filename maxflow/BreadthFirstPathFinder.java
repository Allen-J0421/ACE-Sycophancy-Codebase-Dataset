package maxflow;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Optional;
import java.util.Queue;

/**
 * Finds an augmenting path using breadth-first search, so the path with the
 * fewest edges is always chosen. Combining this strategy with
 * {@link FordFulkersonSolver} gives the Edmonds-Karp algorithm, whose running
 * time is independent of the edge capacities.
 */
public final class BreadthFirstPathFinder implements AugmentingPathFinder {

    @Override
    public Optional<AugmentingPath> findPath(ResidualGraph residual, int source, int sink) {
        int n = residual.vertexCount();
        boolean[] visited = new boolean[n];
        int[] parent = new int[n];
        Arrays.fill(parent, -1);

        Queue<Integer> queue = new ArrayDeque<>();
        queue.add(source);
        visited[source] = true;

        while (!queue.isEmpty()) {
            int u = queue.poll();
            for (int v = 0; v < n; v++) {
                if (!visited[v] && residual.hasResidualCapacity(u, v)) {
                    parent[v] = u;
                    if (v == sink) {
                        return Optional.of(AugmentingPath.fromParents(parent, source, sink, residual));
                    }
                    visited[v] = true;
                    queue.add(v);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public String name() {
        return "breadth-first (Edmonds-Karp)";
    }
}
