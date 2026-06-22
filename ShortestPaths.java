import java.util.ArrayList;
import java.util.List;

/**
 * The result of an all-pairs shortest-path computation: the distance between
 * every pair of vertices, plus enough information to reconstruct an actual
 * shortest path as a sequence of vertices.
 *
 * <p>Instances are produced by {@link FloydWarshall#shortestPaths(Graph)} and
 * are immutable from a caller's perspective: the internal matrices are never
 * exposed by reference.
 */
public final class ShortestPaths {

    /** Sentinel for {@code next} cells with no onward hop (no path). */
    static final int NO_PATH = -1;

    private final int[][] dist;
    private final int[][] next;

    /**
     * Takes ownership of the given matrices; they must not be retained or
     * mutated by the caller afterwards. Package-private by design — only the
     * algorithm constructs results.
     *
     * @param dist shortest-path distances ({@link Graph#INF} when unreachable)
     * @param next {@code next[i][j]} is the first vertex after {@code i} on a
     *             shortest path to {@code j}, or {@link #NO_PATH}
     */
    ShortestPaths(int[][] dist, int[][] next) {
        this.dist = dist;
        this.next = next;
    }

    /** Number of vertices. */
    public int size() {
        return dist.length;
    }

    /**
     * Shortest-path distance from {@code i} to {@code j}, or {@link Graph#INF}
     * if {@code j} is unreachable from {@code i}.
     */
    public int distance(int i, int j) {
        return dist[i][j];
    }

    /** Whether any path exists from {@code i} to {@code j}. */
    public boolean hasPath(int i, int j) {
        return i == j || next[i][j] != NO_PATH;
    }

    /**
     * Reconstructs a shortest path from {@code i} to {@code j} as the sequence
     * of vertices visited, inclusive of both endpoints.
     *
     * @return the vertex sequence, {@code [i]} when {@code i == j}, or an empty
     *         list when {@code j} is unreachable from {@code i}
     */
    public List<Integer> path(int i, int j) {
        if (!hasPath(i, j)) {
            return List.of();
        }
        List<Integer> path = new ArrayList<>();
        int at = i;
        path.add(at);
        while (at != j) {
            at = next[at][j];
            path.add(at);
        }
        return path;
    }

    /** A {@link Graph} view of the shortest-path distance matrix. */
    public Graph distances() {
        return Graph.of(dist);
    }
}
