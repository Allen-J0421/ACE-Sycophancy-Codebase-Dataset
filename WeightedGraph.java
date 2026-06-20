import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * An immutable directed, weighted graph over vertices {@code 0 .. vertices()-1}.
 *
 * <p>Construction validates the vertex count and that every edge endpoint refers
 * to a vertex that actually exists, so the algorithm never has to defend against
 * malformed input.
 */
final class WeightedGraph {

    private final int vertices;
    private final List<WeightedEdge> edges;

    private WeightedGraph(int vertices, List<WeightedEdge> edges) {
        this.vertices = vertices;
        this.edges = Collections.unmodifiableList(edges);
    }

    /** Builds a graph from a {@code {from, to, weight}} adjacency table. */
    static WeightedGraph from(int vertices, int[][] edgeData) {
        List<WeightedEdge> edges = new ArrayList<>(edgeData.length);
        for (int[] row : edgeData) {
            if (row.length != 3) {
                throw new IllegalArgumentException(
                    "each edge must be {from, to, weight}, got length " + row.length);
            }
            edges.add(WeightedEdge.of(row[0], row[1], row[2]));
        }
        return create(vertices, edges);
    }

    /** Builds a graph from explicit {@link WeightedEdge} values. */
    static WeightedGraph of(int vertices, WeightedEdge... edges) {
        return create(vertices, new ArrayList<>(Arrays.asList(edges)));
    }

    private static WeightedGraph create(int vertices, List<WeightedEdge> edges) {
        if (vertices <= 0) {
            throw new IllegalArgumentException("vertices must be positive, got " + vertices);
        }
        for (WeightedEdge edge : edges) {
            requireVertex(edge.from(), vertices, "from");
            requireVertex(edge.to(), vertices, "to");
        }
        return new WeightedGraph(vertices, edges);
    }

    private static void requireVertex(int vertex, int vertices, String role) {
        if (vertex < 0 || vertex >= vertices) {
            throw new IllegalArgumentException(
                "edge " + role + " vertex " + vertex + " out of range [0, " + vertices + ")");
        }
    }

    int vertices() {
        return vertices;
    }

    List<WeightedEdge> edges() {
        return edges;
    }
}
