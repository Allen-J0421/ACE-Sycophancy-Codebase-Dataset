package graph;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * An immutable undirected graph backed by an adjacency list.
 *
 * <p>Vertices are identified by the integers {@code 0 .. vertexCount() - 1}.
 * A {@code Graph} describes structure but does not define it: instances are
 * assembled with a {@link GraphBuilder}, and once built a graph cannot change.
 * The adjacency list is not exposed directly; callers read neighbours through
 * {@link #neighbours(int)}, which returns an unmodifiable view.
 */
public final class Graph {

    private final List<List<Integer>> adjacency;

    /**
     * Wraps an assembled adjacency list. Package-private: graphs are created
     * through {@link GraphBuilder}, which owns construction and validation.
     * The builder hands over freshly built lists and retains no reference to
     * them, so the new graph has sole ownership.
     */
    Graph(List<List<Integer>> adjacency) {
        this.adjacency = adjacency;
    }

    /** Returns the number of vertices in the graph. */
    public int vertexCount() {
        return adjacency.size();
    }

    /** Returns an unmodifiable view of the neighbours of {@code vertex}. */
    public List<Integer> neighbours(int vertex) {
        return Collections.unmodifiableList(adjacency.get(Objects.checkIndex(vertex, adjacency.size())));
    }
}
