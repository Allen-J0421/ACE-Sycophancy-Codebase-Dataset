import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An immutable directed graph represented as a dense distance matrix.
 *
 * <p>Cell {@code matrix[i][j]} holds the weight of the edge from vertex
 * {@code i} to vertex {@code j}. The absence of an edge is encoded by
 * {@link #INF}, and the diagonal is expected to be zero (no cost to remain
 * at a vertex). Edge weights may be negative, but the matrix must be square.
 */
public final class Graph {

    /**
     * Sentinel weight for "no edge". Chosen well below {@link Integer#MAX_VALUE}
     * so that two such values can be summed without overflowing an {@code int}.
     */
    public static final int INF = (int) 1e8;

    private final int[][] matrix;

    private Graph(int[][] matrix) {
        this.matrix = matrix;
    }

    /**
     * Creates a graph from a square distance matrix.
     *
     * @param matrix the distance matrix; defensively copied, so the caller may
     *               safely mutate the argument afterwards
     * @throws IllegalArgumentException if the matrix is null, empty, or not square
     */
    public static Graph of(int[][] matrix) {
        if (matrix == null || matrix.length == 0) {
            throw new IllegalArgumentException("matrix must be non-empty");
        }
        int n = matrix.length;
        int[][] copy = new int[n][];
        for (int i = 0; i < n; i++) {
            if (matrix[i] == null || matrix[i].length != n) {
                throw new IllegalArgumentException(
                        "matrix must be square: row " + i + " has length "
                                + (matrix[i] == null ? "null" : matrix[i].length)
                                + ", expected " + n);
            }
            copy[i] = matrix[i].clone();
        }
        return new Graph(copy);
    }

    /** Number of vertices in the graph. */
    public int size() {
        return matrix.length;
    }

    /**
     * Weight of the edge from {@code i} to {@code j}, or {@link #INF} if absent.
     *
     * @throws IndexOutOfBoundsException if either vertex is not in {@code [0, size()-1]}
     */
    public int weight(int i, int j) {
        Vertices.requireValid(i, matrix.length, "source");
        Vertices.requireValid(j, matrix.length, "target");
        return matrix[i][j];
    }

    /**
     * Returns the vertices directly reachable from {@code vertex} via a single
     * outgoing edge, in ascending order. A vertex is never its own neighbor.
     *
     * @param vertex the source vertex
     * @return a new, modifiable list of adjacent vertices (empty if none)
     * @throws IndexOutOfBoundsException if {@code vertex} is not in {@code [0, size()-1]}
     */
    public List<Integer> neighbors(int vertex) {
        Vertices.requireValid(vertex, matrix.length, "vertex");
        List<Integer> result = new ArrayList<>();
        for (int j = 0; j < matrix.length; j++) {
            if (j != vertex && matrix[vertex][j] != INF) {
                result.add(j);
            }
        }
        return result;
    }

    /**
     * Returns a defensive copy of the underlying matrix. Mutating the returned
     * array does not affect this graph.
     */
    public int[][] toMatrix() {
        int[][] copy = new int[matrix.length][];
        for (int i = 0; i < matrix.length; i++) {
            copy[i] = matrix[i].clone();
        }
        return copy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Graph other)) return false;
        return Arrays.deepEquals(matrix, other.matrix);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(matrix);
    }
}
