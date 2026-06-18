import java.util.List;
import java.util.Collections;

public class TraversalResult {
    private final List<Integer> vertices;
    private final int componentCount;

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

    public void print() {
        System.out.print("Traversal: ");
        for (int vertex : vertices) {
            System.out.print(vertex + " ");
        }
        System.out.println();
        System.out.println("Connected components: " + componentCount);
    }
}
