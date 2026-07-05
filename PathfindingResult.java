import java.util.Arrays;

record PathfindingResult(int[] distances) {
    static final int UNREACHABLE = Integer.MAX_VALUE;

    PathfindingResult {
        distances = distances.clone();
    }

    int distanceTo(int vertex) {
        return distances[vertex];
    }

    boolean isReachable(int vertex) {
        return distanceTo(vertex) != UNREACHABLE;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof PathfindingResult r && Arrays.equals(distances, r.distances);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(distances);
    }
}
