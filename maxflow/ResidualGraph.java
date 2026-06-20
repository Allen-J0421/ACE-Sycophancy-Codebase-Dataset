package maxflow;

public interface ResidualGraph {
    int vertexCount();

    int residualCapacity(int from, int to);

    boolean hasResidualCapacity(int from, int to);

    int augmentPath(AugmentingPath path);

    int[][] snapshotCapacities();
}
