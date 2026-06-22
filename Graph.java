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

    /**
     * Starts building a graph by adding edges one at a time, which is clearer
     * and less error-prone than writing out a dense matrix of {@link #INF}
     * sentinels by hand.
     *
     * @param vertexCount the number of vertices; must be positive
     * @throws IllegalArgumentException if {@code vertexCount} is not positive
     */
    public static Builder builder(int vertexCount) {
        return new Builder(vertexCount);
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

    /**
     * Fluent builder for {@link Graph}. Starts from a graph with no edges (every
     * off-diagonal weight {@link #INF}, the diagonal {@code 0}) and lets callers
     * add directed edges incrementally.
     */
    public static final class Builder {

        private final int[][] matrix;

        private Builder(int vertexCount) {
            if (vertexCount <= 0) {
                throw new IllegalArgumentException(
                        "vertexCount must be positive, got " + vertexCount);
            }
            matrix = new int[vertexCount][vertexCount];
            for (int i = 0; i < vertexCount; i++) {
                Arrays.fill(matrix[i], INF);
                matrix[i][i] = 0;
            }
        }

        /**
         * Adds (or overwrites) the directed edge {@code from -> to}.
         *
         * @param from   the source vertex
         * @param to     the target vertex (must differ from {@code from})
         * @param weight the edge weight; may be negative but must be less than
         *               {@link #INF}, which is reserved to mean "no edge"
         * @return this builder, for chaining
         * @throws IndexOutOfBoundsException if a vertex is out of range
         * @throws IllegalArgumentException  if {@code from == to} or the weight
         *                                   is not less than {@link #INF}
         */
        public Builder addEdge(int from, int to, int weight) {
            Vertices.requireValid(from, matrix.length, "source");
            Vertices.requireValid(to, matrix.length, "target");
            if (from == to) {
                throw new IllegalArgumentException(
                        "self-loops are not supported (from == to == " + from + ")");
            }
            if (weight >= INF) {
                throw new IllegalArgumentException(
                        "edge weight " + weight + " must be less than INF (" + INF + ")");
            }
            matrix[from][to] = weight;
            return this;
        }

        /**
         * Builds an immutable {@link Graph} from the edges added so far. The
         * builder may continue to be used afterwards without affecting the
         * returned graph.
         */
        public Graph build() {
            return Graph.of(matrix);
        }
    }
}
