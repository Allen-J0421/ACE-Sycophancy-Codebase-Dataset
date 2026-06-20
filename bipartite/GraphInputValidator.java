package bipartite;

import java.util.List;

/**
 * Validates the inputs used to build a graph, failing fast with descriptive
 * messages instead of surfacing low-level errors (such as array index
 * exceptions) deep inside graph construction. It works with any
 * {@link GraphEdge}, so every graph implementation shares the same checks.
 */
final class GraphInputValidator {

    private GraphInputValidator() {
    }

    /**
     * @param order the number of vertices; vertices are {@code [0, order)}
     * @param edges the edges to be added to the graph
     * @throws IllegalArgumentException if the order is negative, the edge list
     *                                  is null or contains a null edge, or any
     *                                  edge references a vertex outside
     *                                  {@code [0, order)}
     */
    static void validate(int order, List<? extends GraphEdge> edges) {
        if (order < 0) {
            throw new IllegalArgumentException("Vertex count must be non-negative, got " + order);
        }
        if (edges == null) {
            throw new IllegalArgumentException("Edge list must not be null");
        }
        for (GraphEdge edge : edges) {
            if (edge == null) {
                throw new IllegalArgumentException("Edge list must not contain null edges");
            }
            if (edge.u() < 0 || edge.v() < 0 || edge.u() >= order || edge.v() >= order) {
                throw new IllegalArgumentException(
                        "Edge (" + edge.u() + ", " + edge.v()
                                + ") references a vertex outside the range [0, " + order + ")");
            }
        }
    }
}
