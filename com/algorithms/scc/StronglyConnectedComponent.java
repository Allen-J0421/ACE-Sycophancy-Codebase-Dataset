package com.algorithms.scc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * A single strongly connected component: a maximal set of vertices that are
 * all mutually reachable from one another.
 *
 * <p>A component is a value object whose identity is its <em>set</em> of
 * vertices: two components are equal when they contain the same vertices,
 * regardless of the order the traversal happened to discover them in. The
 * insertion-ordered {@link #vertices()} list is retained for stable output.
 *
 * @param <V> the vertex label type
 */
public final class StronglyConnectedComponent<V> implements Iterable<V> {

    private final List<V> vertices;
    private final Set<V> membership;

    public StronglyConnectedComponent(List<V> vertices) {
        if (vertices == null || vertices.isEmpty()) {
            throw new IllegalArgumentException("A component must contain at least one vertex");
        }
        this.vertices = Collections.unmodifiableList(new ArrayList<>(vertices));
        // Backing set gives O(1) membership tests and defines value equality.
        this.membership = Collections.unmodifiableSet(new LinkedHashSet<>(vertices));
    }

    /** @return the vertices in this component, in discovery order (unmodifiable). */
    public List<V> vertices() {
        return vertices;
    }

    /** @return the number of vertices in this component. */
    public int size() {
        return membership.size();
    }

    /**
     * @param vertex a vertex
     * @return {@code true} if the vertex belongs to this component (O(1))
     */
    public boolean contains(V vertex) {
        return membership.contains(vertex);
    }

    @Override
    public Iterator<V> iterator() {
        return vertices.iterator();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StronglyConnectedComponent)) return false;
        return membership.equals(((StronglyConnectedComponent<?>) o).membership);
    }

    @Override
    public int hashCode() {
        return membership.hashCode();
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
