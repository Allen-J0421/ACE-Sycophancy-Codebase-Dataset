import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * An immutable directed graph over vertices {@code 0 .. vertices()-1}, stored as
 * an adjacency list: {@code neighbors(u)} are the out-vertices reachable from
 * {@code u} by a single edge.
 *
 * <p>Parallel edges are permitted and self-loops are allowed (a self-loop is a
 * one-vertex cycle). Construction validates the vertex count and every edge
 * endpoint, so algorithms operating on the graph never have to defend against
 * malformed input.
 *
 * <p>Build a graph either in one shot with {@link #from(int, int[][])} or
 * fluently with a {@link Builder} from {@link #builder(int)} / {@link #builder()}.
 */
final class DirectedGraph {

    private final List<List<Integer>> adjacency;

    private DirectedGraph(List<List<Integer>> adjacency) {
        this.adjacency = adjacency;
    }

    /**
     * Builds a graph with {@code vertexCount} vertices and the given directed
     * edges, each expressed as a {@code {from, to}} pair. Equivalent to
     * {@code builder(vertexCount).addEdges(edges).build()}.
     *
     * @throws IllegalArgumentException if {@code vertexCount} is negative or an
     *                                  edge is not a two-element {@code {from, to}} pair
     * @throws IndexOutOfBoundsException if an edge endpoint is not a valid vertex
     */
    static DirectedGraph from(int vertexCount, int[][] edges) {
        return builder(vertexCount).addEdges(edges).build();
    }

    /**
     * Starts a fluent builder for a graph with a fixed {@code vertexCount}. Edge
     * endpoints are validated against this count as they are added.
     *
     * @throws IllegalArgumentException if {@code vertexCount} is negative
     */
    static Builder builder(int vertexCount) {
        return new Builder(vertexCount, true);
    }

    /**
     * Starts a fluent builder whose vertex set grows to fit the edges added to
     * it (the final count is one past the highest vertex referenced). Use
     * {@link Builder#ensureVertices(int)} to reserve isolated trailing vertices.
     */
    static Builder builder() {
        return new Builder(0, false);
    }

    /** Returns the number of vertices in the graph. */
    int vertices() {
        return adjacency.size();
    }

    /** Returns an unmodifiable view of the out-neighbors of {@code vertex}. */
    List<Integer> neighbors(int vertex) {
        checkVertex(vertex, adjacency.size());
        return Collections.unmodifiableList(adjacency.get(vertex));
    }

    private static int checkVertex(int vertex, int vertexCount) {
        if (vertex < 0 || vertex >= vertexCount) {
            throw new IndexOutOfBoundsException(
                    "vertex " + vertex + " out of range [0, " + vertexCount + ")");
        }
        return vertex;
    }

    /**
     * Fluent builder for {@link DirectedGraph}. Chain {@link #addEdge} /
     * {@link #addEdges} calls and finish with {@link #build()}:
     *
     * <pre>{@code
     *   DirectedGraph g = DirectedGraph.builder(4)
     *       .addEdge(0, 1)
     *       .addEdge(1, 2)
     *       .addEdge(2, 0)
     *       .build();
     * }</pre>
     *
     * <p>A builder is single-use: any mutator (or a second {@code build()}) after
     * {@code build()} throws {@link IllegalStateException}.
     */
    static final class Builder {

        private final boolean fixedSize;
        private int vertexCount;
        private List<List<Integer>> adjacency;
        private boolean built;

        private Builder(int vertexCount, boolean fixedSize) {
            if (vertexCount < 0) {
                throw new IllegalArgumentException("vertexCount must be non-negative: " + vertexCount);
            }
            this.fixedSize = fixedSize;
            this.vertexCount = vertexCount;
            this.adjacency = new ArrayList<>(vertexCount);
            for (int i = 0; i < vertexCount; i++) {
                adjacency.add(new ArrayList<>());
            }
        }

        /**
         * Adds a directed edge {@code from -> to}.
         *
         * <p>In a fixed-size builder both endpoints must be valid vertices; in a
         * growing builder the vertex set expands to include them.
         *
         * @throws IndexOutOfBoundsException if an endpoint is negative, or is out
         *                                   of range in a fixed-size builder
         * @throws IllegalStateException if the builder has already been built
         */
        Builder addEdge(int from, int to) {
            checkNotBuilt();
            adjacency.get(resolve(from)).add(resolve(to));
            return this;
        }

        /**
         * Adds every edge in {@code edges}, each a {@code {from, to}} pair.
         *
         * @throws IllegalArgumentException if an edge is not a two-element pair
         */
        Builder addEdges(int[][] edges) {
            checkNotBuilt();
            for (int[] edge : edges) {
                if (edge == null || edge.length != 2) {
                    throw new IllegalArgumentException(
                            "each edge must be a {from, to} pair, got " + Arrays.toString(edge));
                }
                addEdge(edge[0], edge[1]);
            }
            return this;
        }

        /**
         * Ensures the built graph has at least {@code count} vertices, reserving
         * isolated ones that no edge references. Never removes vertices.
         *
         * @throws IndexOutOfBoundsException if {@code count} exceeds a fixed-size
         *                                   builder's vertex count
         */
        Builder ensureVertices(int count) {
            checkNotBuilt();
            if (count < 0) {
                throw new IllegalArgumentException("count must be non-negative: " + count);
            }
            grow(count);
            return this;
        }

        /**
         * Builds the immutable graph and consumes this builder.
         *
         * @throws IllegalStateException if the builder has already been built
         */
        DirectedGraph build() {
            checkNotBuilt();
            built = true;
            List<List<Integer>> result = adjacency;
            adjacency = null; // hand ownership to the graph; no defensive copy needed
            return new DirectedGraph(result);
        }

        /** Validates an endpoint, growing the vertex set in a growing builder. */
        private int resolve(int vertex) {
            if (vertex < 0) {
                throw new IndexOutOfBoundsException(
                        "vertex " + vertex + " out of range [0, " + vertexCount + ")");
            }
            if (vertex >= vertexCount) {
                if (fixedSize) {
                    throw new IndexOutOfBoundsException(
                            "vertex " + vertex + " out of range [0, " + vertexCount + ")");
                }
                grow(vertex + 1);
            }
            return vertex;
        }

        private void grow(int newCount) {
            if (fixedSize && newCount > vertexCount) {
                throw new IndexOutOfBoundsException(
                        "cannot grow a fixed-size builder to " + newCount + " vertices");
            }
            while (adjacency.size() < newCount) {
                adjacency.add(new ArrayList<>());
            }
            vertexCount = Math.max(vertexCount, newCount);
        }

        private void checkNotBuilt() {
            if (built) {
                throw new IllegalStateException("builder already consumed by build()");
            }
        }
    }
}
