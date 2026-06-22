package maxflow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An immutable augmenting path through a {@link ResidualGraph}: an ordered list of
 * vertices from a source to a sink together with its bottleneck (the minimum
 * residual capacity along the path, i.e. the amount of flow it can carry).
 */
public final class AugmentingPath {

    private final List<Integer> vertices;
    private final int bottleneck;

    private AugmentingPath(List<Integer> vertices, int bottleneck) {
        this.vertices = List.copyOf(vertices);
        this.bottleneck = bottleneck;
    }

    /**
     * Reconstructs a path from a {@code parent} array produced by a search, then
     * walks it to determine the bottleneck capacity.
     *
     * @param parent  {@code parent[v]} is the vertex preceding {@code v} on the path,
     *                or {@code -1} for the source
     * @param source  the start vertex
     * @param sink    the end vertex
     */
    public static AugmentingPath fromParents(int[] parent, int source, int sink, ResidualGraph residual) {
        List<Integer> reversed = new ArrayList<>();
        int bottleneck = Integer.MAX_VALUE;
        for (int v = sink; v != source; v = parent[v]) {
            int u = parent[v];
            bottleneck = Math.min(bottleneck, residual.residualCapacity(u, v));
            reversed.add(v);
        }
        reversed.add(source);
        Collections.reverse(reversed);
        return new AugmentingPath(reversed, bottleneck);
    }

    /** Returns the vertices of the path in order from source to sink. */
    public List<Integer> vertices() {
        return vertices;
    }

    /** Returns the maximum amount of flow that can be pushed along this path. */
    public int bottleneck() {
        return bottleneck;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < vertices.size(); i++) {
            if (i > 0) {
                sb.append(" -> ");
            }
            sb.append(vertices.get(i));
        }
        return sb.append(" (bottleneck ").append(bottleneck).append(')').toString();
    }
}
