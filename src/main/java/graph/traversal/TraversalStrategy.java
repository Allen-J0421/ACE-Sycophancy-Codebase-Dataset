package graph.traversal;

import graph.Graph;
import java.util.List;

/**
 * A strategy for traversing the vertices reachable from a source vertex.
 *
 * <p>Implementations differ only in the <em>order</em> in which they visit
 * vertices (for example breadth-first versus depth-first); they all discover
 * the same set — the connected component containing the source.
 */
public interface TraversalStrategy {

    /**
     * Visits every vertex reachable from {@code source} that is not already
     * marked in {@code visited}, in this strategy's order.
     *
     * <p>The {@code visited} array is shared with the caller: this method marks
     * every vertex it visits (including {@code source}) as {@code true}, which
     * lets a caller traverse a whole graph component by component without
     * revisiting vertices. {@code source} is expected to be unvisited on entry.
     *
     * @return the reachable vertices, in visitation order
     */
    List<Integer> traverseFrom(Graph graph, int source, boolean[] visited);
}
