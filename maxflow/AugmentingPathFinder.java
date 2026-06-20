package maxflow;

public interface AugmentingPathFinder {
    boolean findPath(ResidualNetwork residualNetwork, int source, int sink, int[] parent);
}
