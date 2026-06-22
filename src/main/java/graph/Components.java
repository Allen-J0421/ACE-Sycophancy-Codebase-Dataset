package graph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * The connected components of an undirected {@link Graph}: an immutable
 * partition of every vertex into groups that are mutually reachable.
 *
 * <p>Components are navigated through a single, consistent surface — iterate the
 * partition directly ({@code for (Component c : components)}) or via
 * {@link #stream()}, fetch one by id with {@link #get(int)}, or look up the
 * component owning a vertex with {@link #componentContaining(int)}. The
 * underlying representation is never exposed. Membership questions (how many
 * components, which one a vertex is in, whether two vertices are connected) are
 * answered in constant time.
 *
 * <p>Instances are produced by {@link ConnectedComponentsFinder}; the
 * constructor is package-private so a {@code Components} can never describe a
 * partition that does not cover its graph.
 */
public final class Components implements Iterable<Component> {

    private final List<Component> components;
    private final int[] componentOfVertex;

    /**
     * @param vertexGroups the components, each a list of the vertices it contains
     * @param vertexCount the number of vertices the components partition
     */
    Components(List<List<Integer>> vertexGroups, int vertexCount) {
        List<Component> built = new ArrayList<>(vertexGroups.size());
        int[] index = new int[vertexCount];
        for (int id = 0; id < vertexGroups.size(); id++) {
            built.add(new Component(id, vertexGroups.get(id)));
            for (int vertex : vertexGroups.get(id)) {
                index[vertex] = id;
            }
        }
        this.components = List.copyOf(built);
        this.componentOfVertex = index;
    }

    /** Returns the number of connected components. */
    public int count() {
        return components.size();
    }

    /**
     * Returns the component with the given id.
     *
     * @throws IndexOutOfBoundsException if {@code id} is not in {@code 0 .. count() - 1}
     */
    public Component get(int id) {
        return components.get(Objects.checkIndex(id, components.size()));
    }

    /** Returns the components as a stream. */
    public Stream<Component> stream() {
        return components.stream();
    }

    @Override
    public Iterator<Component> iterator() {
        return components.iterator();
    }

    /**
     * Returns the identifier of the component containing {@code vertex}, in the
     * range {@code 0 .. count() - 1}.
     *
     * @throws IndexOutOfBoundsException if {@code vertex} is not a valid vertex
     */
    public int componentOf(int vertex) {
        return componentOfVertex[Objects.checkIndex(vertex, componentOfVertex.length)];
    }

    /**
     * Returns the component containing {@code vertex}.
     *
     * @throws IndexOutOfBoundsException if {@code vertex} is not a valid vertex
     */
    public Component componentContaining(int vertex) {
        return components.get(componentOf(vertex));
    }

    /** Returns whether {@code u} and {@code v} lie in the same component. */
    public boolean connected(int u, int v) {
        return componentOf(u) == componentOf(v);
    }
}
