package graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Builds immutable {@link Graph} instances from a fixed vertex count and a list
 * of undirected edges.
 *
 * <p>This separates defining a graph's structure (the responsibility of the
 * builder) from the built graph itself. Edge endpoints are validated against the
 * declared vertex count as they are added, so malformed structure is rejected
 * eagerly. The builder is reusable and each {@link #build()} returns an
 * independent graph.
 *
 * <pre>{@code
 * Graph g = new GraphBuilder(4)
 *     .addEdge(0, 1)
 *     .addEdges(new int[][] {{1, 2}, {2, 3}})
 *     .build();
 * }</pre>
 */
public final class GraphBuilder {

    private final int vertexCount;
    private final List<int[]> edges = new ArrayList<>();

    /**
     * Creates a builder for a graph with the given number of vertices.
     *
     * @throws IllegalArgumentException if {@code vertexCount} is negative
     */
    public GraphBuilder(int vertexCount) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("vertexCount must be non-negative: " + vertexCount);
        }
        this.vertexCount = vertexCount;
    }

    /** Convenience factory equivalent to {@code new GraphBuilder(vertexCount)}. */
    public static GraphBuilder of(int vertexCount) {
        return new GraphBuilder(vertexCount);
    }

    /**
     * Records an undirected edge between {@code u} and {@code v}.
     *
     * @return this builder, for chaining
     * @throws IndexOutOfBoundsException if either endpoint is not a valid vertex
     */
    public GraphBuilder addEdge(int u, int v) {
        edges.add(new int[] {checkVertex(u), checkVertex(v)});
        return this;
    }

    /**
     * Records every edge in an edge list, where each element is a two-element
     * {@code {u, v}} array.
     *
     * @return this builder, for chaining
     * @throws IllegalArgumentException  if any element does not have exactly two endpoints
     * @throws IndexOutOfBoundsException if any endpoint is not a valid vertex
     */
    public GraphBuilder addEdges(int[][] edgeList) {
        for (int[] edge : edgeList) {
            if (edge.length != 2) {
                throw new IllegalArgumentException(
                    "each edge must have exactly two endpoints, got " + edge.length);
            }
            addEdge(edge[0], edge[1]);
        }
        return this;
    }

    /** Builds an immutable {@link Graph} from the recorded vertices and edges. */
    public Graph build() {
        List<List<Integer>> adjacency = new ArrayList<>(vertexCount);
        for (int i = 0; i < vertexCount; i++) {
            adjacency.add(new ArrayList<>());
        }
        for (int[] edge : edges) {
            adjacency.get(edge[0]).add(edge[1]);
            adjacency.get(edge[1]).add(edge[0]);
        }
        return new Graph(adjacency);
    }

    private int checkVertex(int vertex) {
        return Objects.checkIndex(vertex, vertexCount);
    }
}
