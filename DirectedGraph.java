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
 */
final class DirectedGraph {

    private final List<List<Integer>> adjacency;

    private DirectedGraph(List<List<Integer>> adjacency) {
        this.adjacency = adjacency;
    }

    /**
     * Builds a graph with {@code vertexCount} vertices and the given directed
     * edges, each expressed as a {@code {from, to}} pair.
     *
     * @throws IllegalArgumentException if {@code vertexCount} is negative or an
     *                                  edge is not a two-element {@code {from, to}} pair
     * @throws IndexOutOfBoundsException if an edge endpoint is not a valid vertex
     */
    static DirectedGraph from(int vertexCount, int[][] edges) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("vertexCount must be non-negative: " + vertexCount);
        }
        List<List<Integer>> adjacency = new ArrayList<>(vertexCount);
        for (int i = 0; i < vertexCount; i++) {
            adjacency.add(new ArrayList<>());
        }
        for (int[] edge : edges) {
            if (edge == null || edge.length != 2) {
                throw new IllegalArgumentException(
                        "each edge must be a {from, to} pair, got " + Arrays.toString(edge));
            }
            int from = checkVertex(edge[0], vertexCount);
            int to = checkVertex(edge[1], vertexCount);
            adjacency.get(from).add(to);
        }
        return new DirectedGraph(adjacency);
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
}
