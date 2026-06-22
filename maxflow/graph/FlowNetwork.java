package maxflow.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    /** Returns the capacity of the directed edge {@code from -> to} (zero if absent). */
    public Capacity capacity(int from, int to) {
        return Capacity.of(capacity[from][to]);
    }

    /** Returns true if {@code vertex} is a valid vertex identifier. */
    public boolean isVertex(int vertex) {
        return vertex >= 0 && vertex < capacity.length;
    }

    /**
     * Returns the network's capacity-bearing edges as {@link Edge}s (with
     * {@link Edge#value()} being the capacity), in row-major order. This is the
     * representation-independent way to enumerate edges, replacing manual iteration
     * over the capacity matrix.
     */
    public List<Edge> edges() {
        List<Edge> edges = new ArrayList<>();
        for (int from = 0; from < capacity.length; from++) {
            for (int to = 0; to < capacity.length; to++) {
                if (capacity[from][to] > 0) {
                    edges.add(new Edge(from, to, capacity[from][to]));
                }
            }
        }
        return edges;
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
            return addEdge(new Edge(from, to, capacity));
        }

        /**
         * Adds (or overwrites) an edge, interpreting its {@link Edge#value()} as the
         * capacity. The {@code Edge} guarantees distinct, non-negative endpoints and a
         * non-negative value; this method additionally enforces the upper vertex bound.
         *
         * @throws IllegalArgumentException if either endpoint is not a vertex of the
         *         network under construction
         */
        public Builder addEdge(Edge edge) {
            requireVertex(edge.from(), "from");
            requireVertex(edge.to(), "to");
            this.capacity[edge.from()][edge.to()] = edge.value().units();
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
