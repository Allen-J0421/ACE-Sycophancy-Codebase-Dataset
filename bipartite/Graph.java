package bipartite;

import java.util.List;

/**
 * A read-only graph over the vertices {@code [0, order())}, exposing only what
 * graph algorithms need to traverse it. Algorithms such as
 * {@link BipartiteChecker} depend on this interface rather than any concrete
 * representation, so they work unchanged across undirected, directed, and
 * weighted implementations.
 */
public interface Graph {

    /** @return the number of vertices; vertices are the ids {@code [0, order())} */
    int order();

    /**
     * Returns the vertices reachable from {@code vertex} by a single edge. For a
     * directed graph these are the out-neighbors; for an undirected graph the
     * relation is symmetric. Any edge weights are not reported here.
     *
     * @param vertex a vertex id in {@code [0, order())}
     * @return an unmodifiable list of adjacent vertex ids
     * @throws IndexOutOfBoundsException if {@code vertex} is out of range
     */
    List<Integer> neighbors(int vertex);
}
