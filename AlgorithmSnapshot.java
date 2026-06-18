import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

/**
 * Memento class capturing the execution state of a pathfinding algorithm.
 *
 * Enables:
 * - Pausing and resuming algorithm execution
 * - Saving partial algorithm results
 * - Comparing algorithm states
 * - Implementing checkpoint-based execution
 *
 * Pattern: Memento (captures state without exposing internals)
 *
 * Example:
 * {@code
 * // Assuming algorithm had progress tracking (future enhancement)
 * AlgorithmSnapshot snapshot = algorithm.createSnapshot(graph, sourceNode);
 *
 * // Resume later
 * ShortestPathResult result = algorithm.resumeFromSnapshot(snapshot);
 * }
 *
 * @see PathfindingAlgorithm
 * @see SnapshotManager
 */
class AlgorithmSnapshot implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String algorithmName;
    private final int sourceNode;
    private final int vertexCount;
    private final int[] distances;
    private final int[] predecessors;
    private final long executionTimeMillis;
    private final long timestampMillis;
    private final boolean completed;

    AlgorithmSnapshot(String algorithmName, int sourceNode, int vertexCount,
                     int[] distances, int[] predecessors,
                     long executionTimeMillis, boolean completed) {
        this.algorithmName = Objects.requireNonNull(algorithmName);
        this.sourceNode = sourceNode;
        this.vertexCount = vertexCount;
        this.distances = distances != null ? distances.clone() : null;
        this.predecessors = predecessors != null ? predecessors.clone() : null;
        this.executionTimeMillis = executionTimeMillis;
        this.timestampMillis = System.currentTimeMillis();
        this.completed = completed;
    }

    /**
     * Gets algorithm name at time of snapshot.
     */
    String getAlgorithmName() {
        return algorithmName;
    }

    /**
     * Gets source node used in algorithm.
     */
    int getSourceNode() {
        return sourceNode;
    }

    /**
     * Gets vertex count used in algorithm.
     */
    int getVertexCount() {
        return vertexCount;
    }

    /**
     * Gets computed distances at time of snapshot.
     */
    int[] getDistances() {
        return distances != null ? distances.clone() : null;
    }

    /**
     * Gets predecessor array at time of snapshot.
     */
    int[] getPredecessors() {
        return predecessors != null ? predecessors.clone() : null;
    }

    /**
     * Gets execution time when snapshot was taken.
     */
    long getExecutionTimeMillis() {
        return executionTimeMillis;
    }

    /**
     * Gets timestamp when snapshot was created.
     */
    long getTimestampMillis() {
        return timestampMillis;
    }

    /**
     * Checks if algorithm had completed when snapshot was taken.
     */
    boolean isCompleted() {
        return completed;
    }

    /**
     * Checks if two snapshots have identical algorithm state.
     */
    boolean isSameState(AlgorithmSnapshot other) {
        if (!algorithmName.equals(other.algorithmName)) return false;
        if (sourceNode != other.sourceNode) return false;
        if (vertexCount != other.vertexCount) return false;
        if (completed != other.completed) return false;
        if (!Arrays.equals(distances, other.distances)) return false;
        return Arrays.equals(predecessors, other.predecessors);
    }

    /**
     * Gets human-readable description of snapshot state.
     */
    String describe() {
        return String.format(
            "AlgorithmSnapshot{%s from %d, vertices=%d, " +
            "distances=%s, completed=%s, time=%d ms}",
            algorithmName, sourceNode, vertexCount,
            Arrays.toString(distances), completed, executionTimeMillis
        );
    }

    @Override
    public String toString() {
        return String.format("AlgorithmSnapshot{%s, source=%d, vertices=%d, " +
                           "completed=%s, timestamp=%d}",
                           algorithmName, sourceNode, vertexCount,
                           completed, timestampMillis);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AlgorithmSnapshot)) return false;
        AlgorithmSnapshot other = (AlgorithmSnapshot) obj;
        return isSameState(other);
    }

    @Override
    public int hashCode() {
        return Objects.hash(algorithmName, sourceNode, vertexCount,
                          Arrays.hashCode(distances), completed);
    }
}
