package graph.algorithm;

import java.util.List;

public class TraversalStats {
    private final String algorithmName;
    private final List<Integer> traversalOrder;
    private final long executionTimeMs;
    private final int verticesVisited;
    private final int edgesTraversed;

    public TraversalStats(String algorithmName, List<Integer> traversalOrder, long executionTimeMs) {
        this.algorithmName = algorithmName;
        this.traversalOrder = traversalOrder;
        this.executionTimeMs = executionTimeMs;
        this.verticesVisited = traversalOrder.size();
        this.edgesTraversed = Math.max(0, traversalOrder.size() - 1);
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    public List<Integer> getTraversalOrder() {
        return traversalOrder;
    }

    public long getExecutionTimeMs() {
        return executionTimeMs;
    }

    public int getVerticesVisited() {
        return verticesVisited;
    }

    public int getEdgesTraversed() {
        return edgesTraversed;
    }

    public String getReport() {
        StringBuilder sb = new StringBuilder();
        sb.append(algorithmName).append(" Statistics:\n");
        sb.append("  Execution Time: ").append(executionTimeMs).append(" ms\n");
        sb.append("  Vertices Visited: ").append(verticesVisited).append("\n");
        sb.append("  Edges Traversed: ").append(edgesTraversed).append("\n");
        sb.append("  Order: ").append(traversalOrder).append("\n");
        return sb.toString();
    }
}
