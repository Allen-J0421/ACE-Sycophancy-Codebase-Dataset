import java.util.Arrays;

public final class ShortestPathResult {
    private final boolean negativeCycle;
    private final int[] distances;
    private final boolean[] reachable;

    private ShortestPathResult(boolean negativeCycle, int[] distances, boolean[] reachable) {
        this.negativeCycle = negativeCycle;
        this.distances = distances;
        this.reachable = reachable;
    }

    static ShortestPathResult success(int[] distances, boolean[] reachable) {
        return new ShortestPathResult(false, distances.clone(), reachable.clone());
    }

    static ShortestPathResult negativeCycle() {
        return new ShortestPathResult(true, new int[0], new boolean[0]);
    }

    public boolean hasNegativeCycle() {
        return negativeCycle;
    }

    public int[] distances() {
        return distances.clone();
    }

    public boolean isReachable(int vertex) {
        ensureDistancesAvailable();
        validateVertex(vertex);
        return reachable[vertex];
    }

    public int distanceTo(int vertex) {
        ensureDistancesAvailable();
        validateVertex(vertex);
        if (!reachable[vertex]) {
            throw new IllegalStateException("vertex is unreachable from the source");
        }
        return distances[vertex];
    }

    public int vertexCount() {
        return distances.length;
    }

    @Override
    public String toString() {
        if (negativeCycle) {
            return "ShortestPathResult{negativeCycle=true}";
        }
        return "ShortestPathResult{distances=" + Arrays.toString(distances)
            + ", reachable=" + Arrays.toString(reachable) + "}";
    }

    private void ensureDistancesAvailable() {
        if (negativeCycle) {
            throw new IllegalStateException("distances are unavailable when a negative cycle is detected");
        }
    }

    private void validateVertex(int vertex) {
        if (vertex < 0 || vertex >= distances.length) {
            throw new IllegalArgumentException("vertex must be within the result range");
        }
    }
}
