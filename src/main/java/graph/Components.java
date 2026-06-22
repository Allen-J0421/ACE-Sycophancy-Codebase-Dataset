package graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The connected components of an undirected {@link Graph}: an immutable
 * partition of every vertex into groups that are mutually reachable.
 *
 * <p>Beyond listing the components, this answers the questions callers
 * usually have — how many components there are, which one a vertex belongs
 * to, and whether two vertices are connected — in constant time.
 *
 * <p>Instances are produced by {@link ConnectedComponentsFinder}; the
 * constructor is package-private so a {@code Components} can never describe a
 * partition that does not cover its graph.
 */
public final class Components {

    private final List<List<Integer>> components;
    private final int[] componentOfVertex;

    /**
     * @param components the components, each a list of the vertices it contains
     * @param vertexCount the number of vertices the components partition
     */
    Components(List<List<Integer>> components, int vertexCount) {
        List<List<Integer>> copy = new ArrayList<>(components.size());
        int[] index = new int[vertexCount];
        for (int id = 0; id < components.size(); id++) {
            copy.add(List.copyOf(components.get(id)));
            for (int vertex : components.get(id)) {
                index[vertex] = id;
            }
        }
        this.components = List.copyOf(copy);
        this.componentOfVertex = index;
    }

    /** Returns the number of connected components. */
    public int count() {
        return components.size();
    }

    /**
     * Returns the components as an unmodifiable list of unmodifiable vertex
     * lists. Component order, and vertex order within a component, follow the
     * traversal that produced them.
     */
    public List<List<Integer>> asList() {
        return components;
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

    /** Returns whether {@code u} and {@code v} lie in the same component. */
    public boolean connected(int u, int v) {
        return componentOf(u) == componentOf(v);
    }
}
