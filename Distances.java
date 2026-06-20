/**
 * A successful shortest-path result: the distance from the source to every vertex.
 *
 * <p>Unreachable vertices carry the {@link #UNREACHABLE} sentinel; prefer
 * {@link #isReachable(int)} over comparing against it directly.
 */
final class Distances implements ShortestPathResult {

    /** Sentinel distance for a vertex that cannot be reached from the source. */
    static final int UNREACHABLE = Integer.MAX_VALUE;

    private final int[] distances;

    Distances(int[] distances) {
        this.distances = distances.clone();
    }

    /** Whether {@code vertex} is reachable from the source. */
    boolean isReachable(int vertex) {
        return distances[vertex] != UNREACHABLE;
    }

    /**
     * The shortest distance to {@code vertex}.
     *
     * @return the distance, or {@link #UNREACHABLE} if it cannot be reached
     */
    int distanceTo(int vertex) {
        return distances[vertex];
    }

    /** A defensive copy of all distances; index {@code i} is the distance to vertex {@code i}. */
    int[] all() {
        return distances.clone();
    }

    int vertexCount() {
        return distances.length;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Distances[");
        for (int v = 0; v < distances.length; v++) {
            if (v > 0) {
                sb.append(", ");
            }
            sb.append(v).append('=')
              .append(distances[v] == UNREACHABLE ? "unreachable" : distances[v]);
        }
        return sb.append(']').toString();
    }
}
