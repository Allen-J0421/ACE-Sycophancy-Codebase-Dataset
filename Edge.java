import java.util.Objects;

/**
 * An immutable directed edge between two vertices of type {@code V}.
 *
 * @param <V> the vertex label type
 */
public final class Edge<V> {

    private final V from;
    private final V to;

    public Edge(V from, V to) {
        this.from = Objects.requireNonNull(from, "from vertex must not be null");
        this.to = Objects.requireNonNull(to, "to vertex must not be null");
    }

    public V from() {
        return from;
    }

    public V to() {
        return to;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Edge)) return false;
        Edge<?> other = (Edge<?>) o;
        return from.equals(other.from) && to.equals(other.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to);
    }

    @Override
    public String toString() {
        return from + " -> " + to;
    }
}
