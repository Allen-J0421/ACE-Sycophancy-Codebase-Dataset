import java.util.List;

public final class StronglyConnectedComponent {
    private final List<Vertex> vertices;

    private StronglyConnectedComponent(List<Vertex> vertices) {
        this.vertices = vertices;
    }

    public static StronglyConnectedComponent fromVertices(List<Vertex> vertices) {
        if (vertices == null) {
            throw new IllegalArgumentException("Vertices must not be null.");
        }

        return new StronglyConnectedComponent(List.copyOf(vertices));
    }

    public List<Vertex> vertices() {
        return vertices;
    }

    public int size() {
        return vertices.size();
    }

    public boolean contains(Vertex vertex) {
        return vertices.contains(vertex);
    }
}
