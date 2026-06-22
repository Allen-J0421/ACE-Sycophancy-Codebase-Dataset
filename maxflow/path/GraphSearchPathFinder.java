package maxflow.path;

import maxflow.graph.ResidualGraph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Template for augmenting-path finders that explore the residual graph with a
 * single-source traversal. The traversal skeleton — discovering vertices, recording
 * predecessors and reconstructing the path — lives here; concrete subclasses supply
 * only a {@link Frontier}, which fixes the search order (breadth-first vs depth-first).
 */
abstract class GraphSearchPathFinder implements AugmentingPathFinder {

    /** Returns a fresh frontier for one traversal. */
    protected abstract Frontier newFrontier();

    @Override
    public final Optional<AugmentingPath> findPath(ResidualGraph residual, int source, int sink) {
        int n = residual.vertexCount();
        boolean[] discovered = new boolean[n];
        int[] parent = new int[n];
        Arrays.fill(parent, -1);

        Frontier frontier = newFrontier();
        frontier.add(source);
        discovered[source] = true;

        while (!frontier.isEmpty()) {
            int u = frontier.removeNext();
            if (u == sink) {
                return Optional.of(reconstructPath(residual, parent, source, sink));
            }
            residual.forEachResidualEdge(u, (v, residualCapacity) -> {
                if (!discovered[v]) {
                    discovered[v] = true;
                    parent[v] = u;
                    frontier.add(v);
                }
            });
        }
        return Optional.empty();
    }

    /** Walks the predecessor chain from sink to source, computing the bottleneck. */
    private static AugmentingPath reconstructPath(ResidualGraph residual, int[] parent,
                                                  int source, int sink) {
        List<Integer> vertices = new ArrayList<>();
        int bottleneck = Integer.MAX_VALUE;
        for (int v = sink; v != source; v = parent[v]) {
            bottleneck = Math.min(bottleneck, residual.residualCapacity(parent[v], v));
            vertices.add(v);
        }
        vertices.add(source);
        Collections.reverse(vertices);
        return new AugmentingPath(vertices, bottleneck);
    }
}
