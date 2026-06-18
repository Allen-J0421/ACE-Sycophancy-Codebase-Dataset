import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Memento class capturing the complete state of a Graph.
 *
 * Enables:
 * - Pausing and resuming graph construction
 * - Undoing graph modifications
 * - Saving graph state for later restoration
 * - Comparing graph states
 *
 * Pattern: Memento (captures state without exposing internals)
 *
 * Example:
 * {@code
 * Graph graph = Graph.create(5);
 * graph.addEdge(0, 1, 4);
 *
 * // Capture state
 * GraphSnapshot snapshot = graph.createSnapshot();
 *
 * graph.addEdge(0, 2, 8);
 *
 * // Restore state
 * graph.restoreFromSnapshot(snapshot);
 * // Graph now has only edge (0,1)
 * }
 *
 * @see Graph
 * @see SnapshotManager
 */
class GraphSnapshot implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int vertexCount;
    private final List<EdgeRecord> edges;
    private final long timestampMillis;

    /**
     * Represents an edge in the snapshot.
     */
    static class EdgeRecord implements Serializable {
        private static final long serialVersionUID = 1L;

        final int source;
        final int destination;
        final int weight;

        EdgeRecord(int source, int destination, int weight) {
            this.source = source;
            this.destination = destination;
            this.weight = weight;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof EdgeRecord)) return false;
            EdgeRecord other = (EdgeRecord) obj;
            return source == other.source &&
                   destination == other.destination &&
                   weight == other.weight;
        }

        @Override
        public int hashCode() {
            return Objects.hash(source, destination, weight);
        }

        @Override
        public String toString() {
            return source + "->" + destination + "(w=" + weight + ")";
        }
    }

    GraphSnapshot(int vertexCount, List<EdgeRecord> edges) {
        this.vertexCount = vertexCount;
        this.edges = new ArrayList<>(edges);
        this.timestampMillis = System.currentTimeMillis();
    }

    /**
     * Gets vertex count at time of snapshot.
     */
    int getVertexCount() {
        return vertexCount;
    }

    /**
     * Gets edges at time of snapshot.
     */
    List<EdgeRecord> getEdges() {
        return new ArrayList<>(edges);
    }

    /**
     * Gets total number of edges in snapshot.
     */
    int getEdgeCount() {
        return edges.size();
    }

    /**
     * Gets timestamp when snapshot was created.
     */
    long getTimestampMillis() {
        return timestampMillis;
    }

    /**
     * Checks if two snapshots have identical state.
     */
    boolean isSameState(GraphSnapshot other) {
        if (vertexCount != other.vertexCount) return false;
        if (edges.size() != other.edges.size()) return false;
        return edges.equals(other.edges);
    }

    /**
     * Gets human-readable description of changes from other snapshot.
     */
    String describeChanges(GraphSnapshot other) {
        if (isSameState(other)) {
            return "No changes";
        }

        int edgesDiff = edges.size() - other.edges.size();
        String diff = edgesDiff > 0 ? "+" + edgesDiff : String.valueOf(edgesDiff);
        return "Edges: " + other.edges.size() + " → " + edges.size() + " (" + diff + ")";
    }

    @Override
    public String toString() {
        return String.format("GraphSnapshot{vertices=%d, edges=%d, timestamp=%d}",
                           vertexCount, edges.size(), timestampMillis);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof GraphSnapshot)) return false;
        GraphSnapshot other = (GraphSnapshot) obj;
        return isSameState(other);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vertexCount, edges);
    }
}
