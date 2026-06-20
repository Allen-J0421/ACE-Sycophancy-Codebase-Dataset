public final class AllPairsShortestPaths {

    private final IntSquareMatrix distances;
    private final NextHopTable nextHopTable;

    AllPairsShortestPaths(int[][] distances, int[][] nextHop) {
        this(IntSquareMatrix.from(distances), NextHopTable.from(nextHop));
    }

    AllPairsShortestPaths(IntSquareMatrix distances, NextHopTable nextHopTable) {
        this.distances = distances;
        this.nextHopTable = nextHopTable;
    }

    public int vertexCount() {
        return distances.size();
    }

    public IntSquareMatrix distances() {
        return distances;
    }

    public int distance(int source, int target) {
        return distances.get(source, target);
    }

    public VertexPath path(int source, int target) {
        return nextHopTable.path(source, target);
    }

    public boolean isReachable(int source, int target) {
        return nextHopTable.isReachable(source, target);
    }

    public boolean hasNegativeCycle() {
        for (int i = 0; i < distances.size(); i++) {
            if (distances.get(i, i) < 0) {
                return true;
            }
        }
        return false;
    }
}
