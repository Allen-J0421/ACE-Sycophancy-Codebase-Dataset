import java.util.Arrays;

/**
 * An undirected, weighted graph backed by an adjacency matrix.
 *
 * <p>A weight of {@code 0} means "no edge" between two vertices, matching the
 * convention used by the original adjacency-matrix representation. The matrix
 * is defensively copied on construction so instances are effectively immutable.
 */
public final class WeightedGraph {

    /** Sentinel weight indicating the absence of an edge. */
    public static final int NO_EDGE = 0;

    private final int[][] adjacency;

    private WeightedGraph(int[][] adjacency) {
        this.adjacency = adjacency;
    }

    /**
     * Builds a graph from a square adjacency matrix.
     *
     * @param matrix a non-empty, square weight matrix; cell {@code [i][j]} holds
     *               the weight of the edge between vertices {@code i} and {@code j}
     * @throws IllegalArgumentException if the matrix is empty or not square
     */
    public static WeightedGraph fromAdjacencyMatrix(int[][] matrix) {
        if (matrix == null || matrix.length == 0) {
            throw new IllegalArgumentException("Adjacency matrix must be non-empty");
        }
        int[][] copy = new int[matrix.length][];
        for (int row = 0; row < matrix.length; row++) {
            if (matrix[row] == null || matrix[row].length != matrix.length) {
                throw new IllegalArgumentException(
                        "Adjacency matrix must be square; row " + row + " is malformed");
            }
            copy[row] = Arrays.copyOf(matrix[row], matrix[row].length);
        }
        return new WeightedGraph(copy);
    }

    /** Returns the number of vertices in the graph. */
    public int vertexCount() {
        return adjacency.length;
    }

    /** Returns the weight of the edge between {@code from} and {@code to}. */
    public int weight(int from, int to) {
        return adjacency[from][to];
    }

    /** Returns {@code true} if a (non-zero weight) edge connects the two vertices. */
    public boolean hasEdge(int from, int to) {
        return adjacency[from][to] != NO_EDGE;
    }
}
