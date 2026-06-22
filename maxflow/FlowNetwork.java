package maxflow;

import java.util.Arrays;

/**
 * An immutable directed flow network represented as a capacity matrix.
 *
 * <p>A network has a fixed number of vertices (identified by the integers
 * {@code 0 .. vertexCount() - 1}) and a non-negative capacity for every ordered
 * pair of vertices. The source and sink are <em>not</em> part of the network;
 * they are supplied per query, so a single network can be solved for different
 * source/sink pairs.
 *
 * <p>Instances are created through {@link Builder} and are safe to share.
 */
public final class FlowNetwork {

    private final int[][] capacity;

    private FlowNetwork(int[][] capacity) {
        this.capacity = capacity;
    }

    /** Returns the number of vertices in the network. */
    public int vertexCount() {
        return capacity.length;
    }

    /** Returns the capacity of the directed edge {@code from -> to} (0 if absent). */
    public int capacity(int from, int to) {
        return capacity[from][to];
    }

    /** Returns true if {@code vertex} is a valid vertex identifier. */
    public boolean isVertex(int vertex) {
        return vertex >= 0 && vertex < capacity.length;
    }

    /** Returns a defensive copy of the underlying capacity matrix. */
    public int[][] toMatrix() {
        int[][] copy = new int[capacity.length][];
        for (int i = 0; i < capacity.length; i++) {
            copy[i] = Arrays.copyOf(capacity[i], capacity[i].length);
        }
        return copy;
    }

    /** Creates a builder for a network with the given number of vertices. */
    public static Builder builder(int vertexCount) {
        return new Builder(vertexCount);
    }

    /**
     * Builds a {@link FlowNetwork} from an existing square capacity matrix.
     *
     * @throws IllegalArgumentException if the matrix is not square or contains
     *         a negative capacity
     */
    public static FlowNetwork fromMatrix(int[][] matrix) {
        Builder builder = new Builder(matrix.length);
        for (int from = 0; from < matrix.length; from++) {
            if (matrix[from].length != matrix.length) {
                throw new IllegalArgumentException(
                        "Capacity matrix must be square; row " + from + " has length "
                                + matrix[from].length + ", expected " + matrix.length);
            }
            for (int to = 0; to < matrix.length; to++) {
                if (matrix[from][to] != 0) {
                    builder.addEdge(from, to, matrix[from][to]);
                }
            }
        }
        return builder.build();
    }

    /** Fluent builder that validates edges as they are added. */
    public static final class Builder {

        private final int[][] capacity;

        private Builder(int vertexCount) {
            if (vertexCount <= 0) {
                throw new IllegalArgumentException(
                        "vertexCount must be positive, was " + vertexCount);
            }
            this.capacity = new int[vertexCount][vertexCount];
        }

        /**
         * Adds (or overwrites) the capacity of the directed edge {@code from -> to}.
         *
         * @throws IllegalArgumentException if a vertex is out of range, the edge is
         *         a self-loop, or the capacity is negative
         */
        public Builder addEdge(int from, int to, int capacity) {
            requireVertex(from, "from");
            requireVertex(to, "to");
            if (from == to) {
                throw new IllegalArgumentException("Self-loops are not allowed at vertex " + from);
            }
            if (capacity < 0) {
                throw new IllegalArgumentException(
                        "Capacity must be non-negative, was " + capacity);
            }
            this.capacity[from][to] = capacity;
            return this;
        }

        public FlowNetwork build() {
            int[][] snapshot = new int[capacity.length][];
            for (int i = 0; i < capacity.length; i++) {
                snapshot[i] = Arrays.copyOf(capacity[i], capacity[i].length);
            }
            return new FlowNetwork(snapshot);
        }

        private void requireVertex(int vertex, String name) {
            if (vertex < 0 || vertex >= capacity.length) {
                throw new IllegalArgumentException(
                        name + " vertex " + vertex + " is out of range [0, " + capacity.length + ")");
            }
        }
    }
}
