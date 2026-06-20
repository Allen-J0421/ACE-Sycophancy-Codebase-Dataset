package maxflow;

import java.util.Optional;

public interface AugmentingPathFinder {
    Optional<AugmentingPath> findPath(ResidualNetwork residualNetwork, int source, int sink);
}
