import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * An immutable directed, weighted graph over vertices {@code 0 .. vertices()-1},
 * stored as an adjacency list: {@code adjacency[u]} holds the arcs leaving {@code u}.
 *
 * <p>Each stored {@link Arc} keeps only its target and weight — the source vertex
 * is implied by its position in the array, so the redundant per-edge "from" field
 * is not stored. Vertices with no outgoing arcs share a single empty list rather
 * than each allocating one.
 *
 * <p>Construction validates the vertex count and every edge endpoint, so the
 * algorithm never has to defend against malformed input.
 */
final class WeightedGraph {

    /** An outgoing arc; its source is the index of the adjacency entry holding it. */
    record Arc(int to, int weight) {
    }

    private final int vertices;
    private final List<Arc>[] adjacency;
    private final int edgeCount;

    private WeightedGraph(int vertices, List<Arc>[] adjacency, int edgeCount) {
        this.vertices = vertices;
        this.adjacency = adjacency;
        this.edgeCount = edgeCount;
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
        return create(vertices, Arrays.asList(edges));
    }

    @SuppressWarnings("unchecked") // generic array creation; element type is checked on insert
    private static WeightedGraph create(int vertices, List<WeightedEdge> edges) {
        if (vertices <= 0) {
            throw new IllegalArgumentException("vertices must be positive, got " + vertices);
        }
        List<Arc>[] adjacency = (List<Arc>[]) new List[vertices];
        for (WeightedEdge edge : edges) {
            requireVertex(edge.from(), vertices, "from");
            requireVertex(edge.to(), vertices, "to");
            if (adjacency[edge.from()] == null) {
                adjacency[edge.from()] = new ArrayList<>();
            }
            adjacency[edge.from()].add(new Arc(edge.to(), edge.weight()));
        }
        // Freeze each bucket; edge-less vertices share one immutable empty list.
        for (int u = 0; u < vertices; u++) {
            adjacency[u] = (adjacency[u] == null)
                ? List.of()
                : Collections.unmodifiableList(adjacency[u]);
        }
        return new WeightedGraph(vertices, adjacency, edges.size());
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

    int edgeCount() {
        return edgeCount;
    }

    /** The arcs leaving {@code from}; an empty list if it has none. */
    List<Arc> outgoing(int from) {
        return adjacency[from];
    }

    /**
     * A flat view of every edge, each reconstructed with its source vertex.
     *
     * <p>Bellman-Ford iterates {@link #outgoing(int)} per vertex; this is a
     * convenience for callers that want the whole edge set in one list.
     */
    List<WeightedEdge> edges() {
        List<WeightedEdge> all = new ArrayList<>(edgeCount);
        for (int u = 0; u < vertices; u++) {
            for (Arc arc : adjacency[u]) {
                all.add(WeightedEdge.of(u, arc.to(), arc.weight()));
            }
        }
        return all;
    }
}
