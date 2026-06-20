import java.util.List;
import java.util.Objects;

final class ConnectedComponent {

    private final List<Integer> vertices;

    ConnectedComponent(List<Integer> vertices) {
        Objects.requireNonNull(vertices, "vertices");
        this.vertices = List.copyOf(vertices);
    }

    int size() {
        return vertices.size();
    }

    List<Integer> vertices() {
        return vertices;
    }

    String format() {
        return ComponentFormatter.format(this);
    }

    @Override
    public String toString() {
        return vertices.toString();
    }
}
