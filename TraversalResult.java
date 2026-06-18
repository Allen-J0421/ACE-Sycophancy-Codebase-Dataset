import java.util.List;
import java.util.Collections;

public class TraversalResult {
    private final List<Integer> vertices;
    private final int componentCount;
    private PerformanceMetrics performanceMetrics;

    public TraversalResult(List<Integer> vertices, int componentCount) {
        this.vertices = Collections.unmodifiableList(vertices);
        this.componentCount = componentCount;
    }

    public List<Integer> getVertices() {
        return vertices;
    }

    public int getComponentCount() {
        return componentCount;
    }

    public VertexStream stream() {
        return new VertexStream(vertices);
    }

    public void setPerformanceMetrics(PerformanceMetrics metrics) {
        this.performanceMetrics = metrics;
    }

    public PerformanceMetrics getPerformanceMetrics() {
        return performanceMetrics;
    }

    public void print() {
        System.out.print("Traversal: ");
        for (int vertex : vertices) {
            System.out.print(vertex + " ");
        }
        System.out.println();
        System.out.println("Connected components: " + componentCount);

        if (performanceMetrics != null) {
            System.out.println("Duration: " + performanceMetrics.getDurationMillis() + " ms");
        }
    }
}
