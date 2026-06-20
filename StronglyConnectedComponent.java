import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A single strongly connected component: a maximal set of vertices that are
 * all mutually reachable from one another.
 *
 * @param <V> the vertex label type
 */
public final class StronglyConnectedComponent<V> {

    private final List<V> vertices;

    public StronglyConnectedComponent(List<V> vertices) {
        if (vertices == null || vertices.isEmpty()) {
            throw new IllegalArgumentException("A component must contain at least one vertex");
        }
        this.vertices = Collections.unmodifiableList(new ArrayList<>(vertices));
    }

    /** @return the vertices in this component (unmodifiable). */
    public List<V> vertices() {
        return vertices;
    }

    /** @return the number of vertices in this component. */
    public int size() {
        return vertices.size();
    }

    /**
     * @param vertex a vertex
     * @return {@code true} if the vertex belongs to this component
     */
    public boolean contains(V vertex) {
        return vertices.contains(vertex);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < vertices.size(); i++) {
            if (i > 0) sb.append(' ');
            sb.append(vertices.get(i));
        }
        return sb.toString();
    }
}
