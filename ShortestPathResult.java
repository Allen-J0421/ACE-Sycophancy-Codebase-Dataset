import java.util.Arrays;

public final class ShortestPathResult {
    private final boolean negativeCycle;
    private final int[] distances;

    private ShortestPathResult(boolean negativeCycle, int[] distances) {
        this.negativeCycle = negativeCycle;
        this.distances = distances;
    }

    static ShortestPathResult success(int[] distances) {
        return new ShortestPathResult(false, distances.clone());
    }

    static ShortestPathResult negativeCycle() {
        return new ShortestPathResult(true, new int[0]);
    }

    public boolean hasNegativeCycle() {
        return negativeCycle;
    }

    public int[] distances() {
        return distances.clone();
    }

    public int distanceTo(int vertex) {
        ensureDistancesAvailable();
        validateVertex(vertex);
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
        return "ShortestPathResult{distances=" + Arrays.toString(distances) + "}";
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
