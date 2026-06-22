package graph;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * A single connected component: an identified, immutable group of vertices that
 * are mutually reachable.
 *
 * <p>A component is navigable both as an {@link Iterable} of its vertices and as
 * an {@link #stream() IntStream}, so callers never need to reach into a raw
 * list representation.
 */
public final class Component implements Iterable<Integer> {

    private final int id;
    private final List<Integer> vertices;

    /**
     * @param id the component's identifier within its {@link Components}
     * @param vertices the vertices it contains (copied defensively)
     */
    Component(int id, List<Integer> vertices) {
        this.id = id;
        this.vertices = List.copyOf(vertices);
    }

    /** Returns this component's identifier, in the range {@code 0 .. count() - 1}. */
    public int id() {
        return id;
    }

    /** Returns the number of vertices in this component. */
    public int size() {
        return vertices.size();
    }

    /** Returns whether {@code vertex} belongs to this component. */
    public boolean contains(int vertex) {
        return vertices.contains(vertex);
    }

    /**
     * Returns this component's vertices as an unmodifiable list, in the order
     * the traversal that produced them visited them.
     */
    public List<Integer> vertices() {
        return vertices;
    }

    /** Returns this component's vertices as a stream. */
    public IntStream stream() {
        return vertices.stream().mapToInt(Integer::intValue);
    }

    @Override
    public Iterator<Integer> iterator() {
        return vertices.iterator();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o instanceof Component other && id == other.id && vertices.equals(other.vertices);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, vertices);
    }

    @Override
    public String toString() {
        return "Component " + id + " " + vertices;
    }
}
