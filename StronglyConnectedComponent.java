import java.util.List;

public final class StronglyConnectedComponent {
    private final List<Integer> vertices;

    private StronglyConnectedComponent(List<Integer> vertices) {
        this.vertices = vertices;
    }

    public static StronglyConnectedComponent fromVertices(List<Integer> vertices) {
        if (vertices == null) {
            throw new IllegalArgumentException("Vertices must not be null.");
        }

        return new StronglyConnectedComponent(List.copyOf(vertices));
    }

    public List<Integer> vertices() {
        return vertices;
    }

    public int size() {
        return vertices.size();
    }

    public boolean contains(int vertex) {
        return vertices.contains(vertex);
    }
}
