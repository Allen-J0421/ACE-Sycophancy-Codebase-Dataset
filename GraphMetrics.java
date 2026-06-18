import java.util.Map;
import java.util.Collections;

public class GraphMetrics {
    private final int vertexCount;
    private final int edgeCount;
    private final int componentCount;
    private final Map<Integer, Integer> vertexDegrees;

    public GraphMetrics(int vertexCount, int edgeCount, int componentCount, Map<Integer, Integer> vertexDegrees) {
        this.vertexCount = vertexCount;
        this.edgeCount = edgeCount;
        this.componentCount = componentCount;
        this.vertexDegrees = Collections.unmodifiableMap(vertexDegrees);
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public int getEdgeCount() {
        return edgeCount;
    }

    public int getComponentCount() {
        return componentCount;
    }

    public Map<Integer, Integer> getVertexDegrees() {
        return vertexDegrees;
    }

    public int getMaxDegree() {
        return vertexDegrees.values().stream().mapToInt(Integer::intValue).max().orElse(0);
    }

    public int getMinDegree() {
        return vertexDegrees.values().stream().mapToInt(Integer::intValue).min().orElse(0);
    }

    public double getAverageDegree() {
        if (vertexCount == 0) return 0;
        return (double) vertexDegrees.values().stream().mapToInt(Integer::intValue).sum() / vertexCount;
    }

    public void print() {
        System.out.println("=== Graph Metrics ===");
        System.out.println("Vertices: " + vertexCount);
        System.out.println("Edges: " + edgeCount);
        System.out.println("Components: " + componentCount);
        System.out.println("Max Degree: " + getMaxDegree());
        System.out.println("Min Degree: " + getMinDegree());
        System.out.printf("Average Degree: %.2f%n", getAverageDegree());
        System.out.println("Vertex Degrees: " + vertexDegrees);
    }
}
