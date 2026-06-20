package maxflow;

import java.util.Optional;

public interface AugmentingPathFinder {
    Optional<AugmentingPath> findPath(ResidualGraph residualGraph, int source, int sink);
}
