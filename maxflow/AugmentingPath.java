package maxflow;

import java.util.List;

/**
 * An immutable augmenting path: an ordered list of vertices from a source to a
 * sink together with its bottleneck (the minimum residual capacity along the
 * path, i.e. the amount of flow it can carry).
 *
 * <p>This is a pure value object with no knowledge of how it was discovered or of
 * the graph it traverses; an {@link AugmentingPathFinder} constructs it.
 */
public final class AugmentingPath {

    private final List<Integer> vertices;
    private final int bottleneck;

    /**
     * @param vertices   the path's vertices in order from source to sink
     *                   (at least two, source first and sink last)
     * @param bottleneck the maximum flow the path can carry; must be positive
     */
    public AugmentingPath(List<Integer> vertices, int bottleneck) {
        if (vertices.size() < 2) {
            throw new IllegalArgumentException("An augmenting path needs at least two vertices");
        }
        if (bottleneck <= 0) {
            throw new IllegalArgumentException("bottleneck must be positive, was " + bottleneck);
        }
        this.vertices = List.copyOf(vertices);
        this.bottleneck = bottleneck;
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
