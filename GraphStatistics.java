import java.util.HashMap;
import java.util.Map;

public class GraphStatistics {
    private final IGraph graph;
    private final Map<Integer, Integer> degreeDistribution;
    private double density;
    private int maxDegree;
    private int minDegree;
    private double avgDegree;

    public GraphStatistics(IGraph graph) {
        this.graph = graph;
        this.degreeDistribution = new HashMap<>();
        calculateStatistics();
    }

    private void calculateStatistics() {
        if (graph.getVertexCount() == 0) {
            return;
        }

        maxDegree = 0;
        minDegree = Integer.MAX_VALUE;
        int totalDegree = 0;

        for (int v : graph.getAllVertices()) {
            int degree = graph.getDegree(v);
            totalDegree += degree;
            maxDegree = Math.max(maxDegree, degree);
            minDegree = Math.min(minDegree, degree);
            degreeDistribution.put(degree, degreeDistribution.getOrDefault(degree, 0) + 1);
        }

        avgDegree = (double) totalDegree / graph.getVertexCount();

        int maxEdges = graph.getVertexCount() * (graph.getVertexCount() - 1) / 2;
        density = maxEdges > 0 ? (double) graph.getEdgeCount() / maxEdges : 0;
    }

    public double getDensity() {
        return density;
    }

    public int getMaxDegree() {
        return maxDegree;
    }

    public int getMinDegree() {
        return minDegree == Integer.MAX_VALUE ? 0 : minDegree;
    }

    public double getAverageDegree() {
        return avgDegree;
    }

    public Map<Integer, Integer> getDegreeDistribution() {
        return new HashMap<>(degreeDistribution);
    }

    public boolean isRegular() {
        return getMaxDegree() == getMinDegree() && getMaxDegree() > 0;
    }

    public boolean isSparse() {
        return density < 0.1;
    }

    public boolean isDense() {
        return density > 0.5;
    }

    public boolean isComplete() {
        int maxEdges = graph.getVertexCount() * (graph.getVertexCount() - 1) / 2;
        return graph.getEdgeCount() == maxEdges;
    }

    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("Graph Statistics:\n");
        sb.append("  Vertices: ").append(graph.getVertexCount()).append("\n");
        sb.append("  Edges: ").append(graph.getEdgeCount()).append("\n");
        sb.append("  Density: ").append(String.format("%.3f", density)).append("\n");
        sb.append("  Avg Degree: ").append(String.format("%.2f", avgDegree)).append("\n");
        sb.append("  Min Degree: ").append(getMinDegree()).append("\n");
        sb.append("  Max Degree: ").append(maxDegree).append("\n");
        sb.append("  Regular: ").append(isRegular()).append("\n");
        sb.append("  Complete: ").append(isComplete()).append("\n");
        sb.append("  Sparse: ").append(isSparse()).append("\n");
        sb.append("  Dense: ").append(isDense()).append("\n");
        return sb.toString();
    }
}
