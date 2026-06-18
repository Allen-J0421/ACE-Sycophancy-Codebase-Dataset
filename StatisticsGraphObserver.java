/**
 * Observer that collects statistics about graph construction.
 *
 * Enables analysis of graph properties without coupling
 * collection logic to graph implementation.
 *
 * Useful for:
 * - Analyzing graph growth patterns
 * - Validating graph structure
 * - Generating reports
 * - Performance monitoring
 */
class StatisticsGraphObserver implements GraphObserver {
    private int totalVertices = 0;
    private int totalEdges = 0;
    private int minWeight = Integer.MAX_VALUE;
    private int maxWeight = Integer.MIN_VALUE;
    private long totalWeight = 0;

    @Override
    public void onGraphCreated(int vertexCount) {
        this.totalVertices = vertexCount;
    }

    @Override
    public void onEdgeAdded(int source, int destination, int weight) {
        totalEdges++;
        minWeight = Math.min(minWeight, weight);
        maxWeight = Math.max(maxWeight, weight);
        totalWeight += weight;
    }

    /**
     * Gets total number of vertices in graph.
     */
    int getTotalVertices() {
        return totalVertices;
    }

    /**
     * Gets total number of edges added.
     */
    int getTotalEdges() {
        return totalEdges;
    }

    /**
     * Gets average edge weight.
     */
    double getAverageWeight() {
        return totalEdges > 0 ? (double) totalWeight / totalEdges : 0;
    }

    /**
     * Gets minimum edge weight.
     */
    int getMinWeight() {
        return minWeight == Integer.MAX_VALUE ? 0 : minWeight;
    }

    /**
     * Gets maximum edge weight.
     */
    int getMaxWeight() {
        return maxWeight == Integer.MIN_VALUE ? 0 : maxWeight;
    }

    /**
     * Gets total of all edge weights.
     */
    long getTotalWeight() {
        return totalWeight;
    }

    /**
     * Gets graph density (actual edges / possible edges).
     */
    double getGraphDensity() {
        if (totalVertices <= 1) return 0;
        int possibleEdges = totalVertices * (totalVertices - 1) / 2;
        return (double) totalEdges / possibleEdges;
    }

    @Override
    public String toString() {
        return String.format(
            "GraphStats{vertices=%d, edges=%d, density=%.2f%%, " +
            "weights=[%d..%d, avg=%.1f]}",
            totalVertices, totalEdges, getGraphDensity() * 100,
            getMinWeight(), getMaxWeight(), getAverageWeight()
        );
    }
}
