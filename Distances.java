import java.util.ArrayDeque;
import java.util.List;

/**
 * A successful shortest-path result: the distance from the source to every vertex,
 * together with enough information to reconstruct the route to any of them.
 *
 * <p>Unreachable vertices carry the {@link #UNREACHABLE} sentinel; prefer
 * {@link #isReachable(int)} over comparing against it directly.
 */
final class Distances implements ShortestPathResult {

    /** Sentinel distance for a vertex that cannot be reached from the source. */
    static final int UNREACHABLE = Integer.MAX_VALUE;

    /** Sentinel predecessor for the source and for unreachable vertices. */
    static final int NO_PREDECESSOR = -1;

    private final int source;
    private final int[] distances;
    private final int[] predecessors;

    Distances(int source, int[] distances, int[] predecessors) {
        this.source = source;
        this.distances = distances.clone();
        this.predecessors = predecessors.clone();
    }

    /** The vertex these distances and paths originate from. */
    int source() {
        return source;
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

    /**
     * Reconstructs the shortest path from the source to {@code target}.
     *
     * <p>The returned {@link Path} lists the vertices from the source through to
     * {@code target} inclusive, paired with the path's total weight. The path to
     * the source itself is the single vertex {@code [source]} with weight {@code 0}.
     *
     * @return the path, or {@link Path#none()} if {@code target} is unreachable
     */
    Path pathTo(int target) {
        if (!isReachable(target)) {
            return Path.none();
        }
        // Walk predecessors back to the source, building the path front-to-back.
        ArrayDeque<Integer> vertices = new ArrayDeque<>();
        for (int v = target; v != NO_PREDECESSOR; v = predecessors[v]) {
            vertices.addFirst(v);
        }
        // The total weight of the shortest path to a vertex is exactly its distance.
        return Path.of(List.copyOf(vertices), distances[target]);
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
