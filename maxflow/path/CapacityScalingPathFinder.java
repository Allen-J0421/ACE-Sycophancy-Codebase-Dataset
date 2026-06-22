package maxflow.path;

import java.util.Optional;

import maxflow.graph.ResidualGraph;

/**
 * Finds augmenting paths by capacity scaling: it prefers paths made of high-residual
 * edges, which lets each augmentation move a lot of flow and keeps the number of
 * augmentations low on networks whose capacities span a wide range.
 *
 * <p>For a threshold {@code Δ} that steps down through the powers of two, the finder
 * looks for a source→sink path using only edges whose residual capacity is at least
 * {@code Δ}. It returns a path from the highest {@code Δ} band that still connects the
 * two — so the path's bottleneck is at least {@code Δ} — and only falls back to thinner
 * edges once the fat ones are exhausted. At {@code Δ == 1} the admitted sub-graph is the
 * full residual graph, so the finder reports no path exactly when none exists.
 *
 * <p>Unlike the textbook formulation, which carries {@code Δ} as mutable state that
 * decreases monotonically across the whole computation, this finder is stateless: it
 * re-derives the starting {@code Δ} on each call. That keeps it thread-safe and freely
 * reusable like the other strategies, at the cost of re-scanning the upper bands — a
 * minor constant factor, not a change in the paths chosen.
 */
public final class CapacityScalingPathFinder implements AugmentingPathFinder {

    @Override
    public Optional<AugmentingPath> findPath(ResidualGraph residual, int source, int sink) {
        for (int threshold = Integer.highestOneBit(maxResidualCapacity(residual));
                threshold >= 1; threshold >>= 1) {
            int delta = threshold;
            Optional<AugmentingPath> path =
                    GraphSearchPathFinder.search(residual, source, sink, Frontier.fifo(),
                            residualCapacity -> residualCapacity >= delta);
            if (path.isPresent()) {
                return path;
            }
        }
        return Optional.empty();
    }

    /** Returns the largest residual capacity of any edge, or zero if there are none. */
    private static int maxResidualCapacity(ResidualGraph residual) {
        int[] max = {0};
        for (int v = 0; v < residual.vertexCount(); v++) {
            residual.forEachResidualEdge(v, (to, residualCapacity) -> {
                if (residualCapacity > max[0]) {
                    max[0] = residualCapacity;
                }
            });
        }
        return max[0];
    }

    @Override
    public String name() {
        return "capacity-scaling";
    }
}
