package maxflow.path;

import maxflow.graph.ResidualGraph;

import java.util.Optional;

/**
 * Strategy for locating an augmenting path from a source to a sink in a
 * {@link ResidualGraph}.
 *
 * <p>The choice of strategy determines the concrete max-flow algorithm: a
 * breadth-first search yields Edmonds-Karp, while a depth-first search yields the
 * textbook Ford-Fulkerson method.
 */
public interface AugmentingPathFinder {

    /**
     * Finds an augmenting path with positive residual capacity, or an empty
     * {@link Optional} if the sink is no longer reachable from the source.
     */
    Optional<AugmentingPath> findPath(ResidualGraph residual, int source, int sink);

    /** Returns a short human-readable name for this strategy. */
    String name();
}
