import java.util.Iterator;
import java.util.List;
import java.util.Objects;

final class ConnectedComponent implements Iterable<Vertex> {

    private final List<Vertex> vertices;

    ConnectedComponent(List<Vertex> vertices) {
        Objects.requireNonNull(vertices, "vertices");
        this.vertices = List.copyOf(vertices);
    }

    int size() {
        return vertices.size();
    }

    boolean isEmpty() {
        return vertices.isEmpty();
    }

    Vertex vertexAt(int vertexIndex) {
        if (vertexIndex < 0 || vertexIndex >= vertices.size()) {
            throw new IllegalArgumentException("Vertex index out of bounds: " + vertexIndex);
        }

        return vertices.get(vertexIndex);
    }

    String format() {
        return ComponentFormatter.format(this);
    }

    @Override
    public Iterator<Vertex> iterator() {
        return vertices.iterator();
    }

    @Override
    public String toString() {
        return vertices.toString();
    }
}
