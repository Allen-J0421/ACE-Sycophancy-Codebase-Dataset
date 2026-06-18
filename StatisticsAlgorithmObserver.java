import java.util.Arrays;

/**
 * Observer that collects statistics about algorithm execution.
 *
 * Enables analysis of algorithm behavior without coupling
 * collection logic to algorithm implementation.
 *
 * Useful for:
 * - Comparing algorithm performance
 * - Analyzing execution patterns
 * - Benchmarking
 * - Validating correctness
 */
class StatisticsAlgorithmObserver implements AlgorithmObserver {
    private String algorithmName = "";
    private int sourceNode = -1;
    private int vertexCount = 0;
    private int verticesProcessed = 0;
    private int totalRelaxations = 0;
    private long executionTimeMillis = 0;
    private int[] finalDistances;

    @Override
    public void onAlgorithmStart(String algorithmName, int sourceNode, int vertexCount) {
        this.algorithmName = algorithmName;
        this.sourceNode = sourceNode;
        this.vertexCount = vertexCount;
        this.verticesProcessed = 0;
        this.totalRelaxations = 0;
    }

    @Override
    public void onVertexProcessed(int vertex, int distance) {
        verticesProcessed++;
    }

    @Override
    public void onEdgeRelaxed(int from, int to, int oldDistance, int newDistance, int relaxationCount) {
        totalRelaxations = relaxationCount;
    }

    @Override
    public void onAlgorithmComplete(int[] distances, int totalRelaxations, long executionTimeMillis) {
        this.finalDistances = distances;
        this.totalRelaxations = totalRelaxations;
        this.executionTimeMillis = executionTimeMillis;
    }

    // Accessors for statistics

    String getAlgorithmName() {
        return algorithmName;
    }

    int getSourceNode() {
        return sourceNode;
    }

    int getVertexCount() {
        return vertexCount;
    }

    int getVerticesProcessed() {
        return verticesProcessed;
    }

    int getTotalRelaxations() {
        return totalRelaxations;
    }

    long getExecutionTimeMillis() {
        return executionTimeMillis;
    }

    int[] getFinalDistances() {
        return finalDistances != null ? finalDistances.clone() : null;
    }

    /**
     * Gets total shortest distance (sum of all distances).
     */
    long getTotalDistance() {
        if (finalDistances == null) return 0;
        long sum = 0;
        for (int dist : finalDistances) {
            if (dist != Integer.MAX_VALUE) {
                sum += dist;
            }
        }
        return sum;
    }

    /**
     * Gets average shortest distance to reachable nodes.
     */
    double getAverageDistance() {
        if (finalDistances == null) return 0;
        int reachableCount = 0;
        long sum = 0;
        for (int dist : finalDistances) {
            if (dist != Integer.MAX_VALUE) {
                sum += dist;
                reachableCount++;
            }
        }
        return reachableCount > 0 ? (double) sum / reachableCount : 0;
    }

    /**
     * Gets count of reachable nodes from source.
     */
    int getReachableCount() {
        if (finalDistances == null) return 0;
        int count = 0;
        for (int dist : finalDistances) {
            if (dist != Integer.MAX_VALUE) count++;
        }
        return count;
    }

    @Override
    public String toString() {
        return String.format(
            "AlgorithmStats{%s from %d, vertices=%d, processed=%d, " +
            "relaxations=%d, reachable=%d, time=%d ms}",
            algorithmName, sourceNode, vertexCount, verticesProcessed,
            totalRelaxations, getReachableCount(), executionTimeMillis
        );
    }
}
