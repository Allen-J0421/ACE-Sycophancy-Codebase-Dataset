import java.util.List;

public class StronglyConnectedComponent {
    private final List<Integer> vertices;

    public StronglyConnectedComponent(List<Integer> vertices) {
        this.vertices = List.copyOf(vertices);
    }

    public List<Integer> getVertices() {
        return vertices;
    }

    public int size() {
        return vertices.size();
    }

    @Override
    public String toString() {
        return vertices.toString();
    }
}
