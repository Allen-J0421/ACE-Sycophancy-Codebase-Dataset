import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class VertexPath {

    private static final VertexPath EMPTY = new VertexPath(Collections.emptyList());

    private final List<Integer> vertices;

    private VertexPath(List<Integer> vertices) {
        this.vertices = Collections.unmodifiableList(new ArrayList<>(vertices));
    }

    public static VertexPath empty() {
        return EMPTY;
    }

    public static VertexPath of(List<Integer> vertices) {
        if (vertices == null) {
            throw new IllegalArgumentException("Path vertices must not be null.");
        }
        if (vertices.isEmpty()) {
            return empty();
        }
        return new VertexPath(vertices);
    }

    public boolean isEmpty() {
        return vertices.isEmpty();
    }

    public int length() {
        return vertices.size();
    }

    public List<Integer> vertices() {
        return vertices;
    }

    @Override
    public String toString() {
        return vertices.toString();
    }
}
