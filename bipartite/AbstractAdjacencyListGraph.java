package bipartite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A generic adjacency-list graph. Every vertex in {@code [0, order())} owns a
 * list of outgoing <em>incidences</em> of type {@code I}; an incidence records
 * one edge leaving that vertex and at minimum identifies its target vertex.
 *
 * <p>This base owns the vertex array and the adjacency storage, leaving two
 * decisions to subclasses:
 * <ul>
 *   <li><b>What an incidence carries</b> — pick {@code I} (e.g. {@code Integer}
 *       for an unweighted graph, or {@link WeightedEdge} for a weighted one) and
 *       implement {@link #targetOf(Object)} to expose its target vertex.</li>
 *   <li><b>Whether edges are symmetric</b> — an undirected subclass adds a
 *       reciprocal incidence for each edge during construction; a directed one
 *       adds only the forward incidence.</li>
 * </ul>
 *
 * <p>Incidences are added only through the protected {@link #addIncidence}, which
 * subclasses invoke from their factory methods. The adjacency is never exposed
 * mutably ({@link #neighbors} returns a fresh list and {@link #incidences} an
 * unmodifiable view), so an instance is effectively immutable once built.
 *
 * @param <I> the per-vertex incidence type
 */
public abstract class AbstractAdjacencyListGraph<I> implements Graph {

    private final List<List<I>> adjacency;

    /**
     * @param order the number of vertices; vertices are {@code [0, order)}
     * @throws IllegalArgumentException if {@code order} is negative
     */
    protected AbstractAdjacencyListGraph(int order) {
        if (order < 0) {
            throw new IllegalArgumentException("Vertex count must be non-negative, got " + order);
        }
        List<List<I>> lists = new ArrayList<>(order);
        for (int i = 0; i < order; i++) {
            lists.add(new ArrayList<>());
        }
        this.adjacency = lists;
    }

    /**
     * Appends one outgoing incidence to {@code source}'s adjacency list. Intended
     * for use by subclass factory methods during construction.
     *
     * @throws IndexOutOfBoundsException if {@code source} is out of range
     */
    protected final void addIncidence(int source, I incidence) {
        requireVertex(source);
        adjacency.get(source).add(incidence);
    }

    /** @return the target vertex that {@code incidence} points to */
    protected abstract int targetOf(I incidence);

    /**
     * @param vertex a vertex id in {@code [0, order())}
     * @return an unmodifiable view of the raw incidences leaving {@code vertex},
     *         preserving any extra data they carry (such as weights)
     * @throws IndexOutOfBoundsException if {@code vertex} is out of range
     */
    protected final List<I> incidences(int vertex) {
        requireVertex(vertex);
        return Collections.unmodifiableList(adjacency.get(vertex));
    }

    @Override
    public final int order() {
        return adjacency.size();
    }

    @Override
    public final List<Integer> neighbors(int vertex) {
        requireVertex(vertex);
        List<I> outgoing = adjacency.get(vertex);
        List<Integer> targets = new ArrayList<>(outgoing.size());
        for (I incidence : outgoing) {
            targets.add(targetOf(incidence));
        }
        return Collections.unmodifiableList(targets);
    }

    /** @throws IndexOutOfBoundsException if {@code vertex} is out of range */
    protected final void requireVertex(int vertex) {
        if (vertex < 0 || vertex >= adjacency.size()) {
            throw new IndexOutOfBoundsException(
                    "Vertex " + vertex + " is outside the range [0, " + adjacency.size() + ")");
        }
    }
}
