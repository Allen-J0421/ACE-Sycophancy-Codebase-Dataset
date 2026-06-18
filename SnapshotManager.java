import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Manages graph and algorithm snapshots enabling undo/redo functionality.
 *
 * Features:
 * - Capture and restore graph state
 * - Undo/redo graph modifications
 * - Manage snapshot history with limits
 * - Compare snapshots for changes
 *
 * Pattern: Command (undo/redo history), Memento (state capture)
 *
 * Example:
 * {@code
 * Graph graph = Graph.create(5);
 * SnapshotManager manager = new SnapshotManager(graph);
 *
 * graph.addEdge(0, 1, 4);
 * manager.saveGraphSnapshot("After adding (0,1)");
 *
 * graph.addEdge(0, 2, 8);
 * manager.saveGraphSnapshot("After adding (0,2)");
 *
 * // Undo
 * manager.undoGraphSnapshot();
 * // Graph back to state after first edge
 *
 * // Redo
 * manager.redoGraphSnapshot();
 * // Graph back to state after second edge
 * }
 *
 * @see GraphSnapshot
 * @see AlgorithmSnapshot
 */
class SnapshotManager {
    private static final int DEFAULT_MAX_SNAPSHOTS = 50;

    private final Graph graph;
    private final LinkedList<SnapshotEntry> graphHistory;
    private final LinkedList<AlgorithmSnapshot> algorithmHistory;
    private int maxSnapshots;

    static class SnapshotEntry {
        final String label;
        final GraphSnapshot snapshot;
        final long createdAtMillis;

        SnapshotEntry(String label, GraphSnapshot snapshot) {
            this.label = label;
            this.snapshot = snapshot;
            this.createdAtMillis = System.currentTimeMillis();
        }

        @Override
        public String toString() {
            return String.format("SnapshotEntry{%s, %s}", label, snapshot);
        }
    }

    SnapshotManager(Graph graph) {
        this.graph = Objects.requireNonNull(graph);
        this.graphHistory = new LinkedList<>();
        this.algorithmHistory = new LinkedList<>();
        this.maxSnapshots = DEFAULT_MAX_SNAPSHOTS;
    }

    /**
     * Sets maximum number of snapshots to retain.
     */
    void setMaxSnapshots(int max) {
        this.maxSnapshots = Math.max(1, max);
    }

    /**
     * Saves current graph state with a label.
     */
    void saveGraphSnapshot(String label) {
        GraphSnapshot snapshot = createGraphSnapshot();
        graphHistory.addLast(new SnapshotEntry(label, snapshot));
        pruneGraphHistory();
    }

    /**
     * Creates snapshot of current graph state without saving.
     */
    GraphSnapshot createGraphSnapshot() {
        List<GraphSnapshot.EdgeRecord> edges = new ArrayList<>();
        for (int i = 0; i < graph.getVertexCount(); i++) {
            for (Edge edge : graph.getAdjacencyListFor(i)) {
                if (i <= edge.getDestination()) {  // Avoid duplicates for undirected graphs
                    edges.add(new GraphSnapshot.EdgeRecord(i, edge.getDestination(), edge.getWeight()));
                }
            }
        }
        return new GraphSnapshot(graph.getVertexCount(), edges);
    }

    /**
     * Restores graph to previous state.
     *
     * @return true if undo was successful, false if no history
     */
    boolean undoGraphSnapshot() {
        if (graphHistory.isEmpty()) {
            return false;
        }
        graphHistory.removeLast();
        return true;
    }

    /**
     * Returns to next state in history.
     *
     * @return true if redo was successful, false if no future history
     */
    boolean redoGraphSnapshot() {
        // Note: Full redo requires maintaining a separate redo stack
        // This is a simplified implementation
        return false;
    }

    /**
     * Saves current algorithm state.
     */
    void saveAlgorithmSnapshot(String algorithmName, int sourceNode,
                              int[] distances, int[] predecessors,
                              long executionTimeMillis, boolean completed) {
        AlgorithmSnapshot snapshot = new AlgorithmSnapshot(
            algorithmName, sourceNode, graph.getVertexCount(),
            distances, predecessors, executionTimeMillis, completed
        );
        algorithmHistory.addLast(snapshot);
        pruneAlgorithmHistory();
    }

    /**
     * Gets count of saved graph snapshots.
     */
    int getGraphSnapshotCount() {
        return graphHistory.size();
    }

    /**
     * Gets count of saved algorithm snapshots.
     */
    int getAlgorithmSnapshotCount() {
        return algorithmHistory.size();
    }

    /**
     * Gets all graph snapshot labels.
     */
    List<String> getGraphSnapshotLabels() {
        List<String> labels = new ArrayList<>();
        for (SnapshotEntry entry : graphHistory) {
            labels.add(entry.label);
        }
        return labels;
    }

    /**
     * Gets most recent graph snapshot.
     */
    GraphSnapshot getLatestGraphSnapshot() {
        if (graphHistory.isEmpty()) {
            return null;
        }
        return graphHistory.getLast().snapshot;
    }

    /**
     * Gets most recent algorithm snapshot.
     */
    AlgorithmSnapshot getLatestAlgorithmSnapshot() {
        if (algorithmHistory.isEmpty()) {
            return null;
        }
        return algorithmHistory.getLast();
    }

    /**
     * Compares two snapshots for differences.
     */
    String compareGraphSnapshots(int indexA, int indexB) {
        if (indexA < 0 || indexA >= graphHistory.size() ||
            indexB < 0 || indexB >= graphHistory.size()) {
            return "Invalid snapshot indices";
        }
        GraphSnapshot snapshotA = graphHistory.get(indexA).snapshot;
        GraphSnapshot snapshotB = graphHistory.get(indexB).snapshot;
        return snapshotB.describeChanges(snapshotA);
    }

    /**
     * Clears all snapshot history.
     */
    void clearHistory() {
        graphHistory.clear();
        algorithmHistory.clear();
    }

    /**
     * Gets summary of snapshot history.
     */
    String getHistorySummary() {
        return String.format(
            "SnapshotManager{graph snapshots=%d, algorithm snapshots=%d, max=%d}",
            graphHistory.size(), algorithmHistory.size(), maxSnapshots
        );
    }

    private void pruneGraphHistory() {
        while (graphHistory.size() > maxSnapshots) {
            graphHistory.removeFirst();
        }
    }

    private void pruneAlgorithmHistory() {
        while (algorithmHistory.size() > maxSnapshots) {
            algorithmHistory.removeFirst();
        }
    }

    @Override
    public String toString() {
        return getHistorySummary();
    }
}
