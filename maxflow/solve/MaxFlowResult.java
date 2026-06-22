package maxflow.solve;

import java.util.List;

import maxflow.graph.Edge;

/**
 * The outcome of a maximum-flow computation: the total flow value, the source and
 * sink it was computed for, and the flow routed along each original edge.
 *
 * <p>A pure, immutable value object; a {@link MaxFlowSolver} populates it. It has
 * no knowledge of the residual graph it was derived from. Each flow edge is an
 * {@link Edge} whose {@link Edge#value()} is the amount of flow on that edge.
 */
public final class MaxFlowResult {

    private final int source;
    private final int sink;
    private final int value;
    private final List<Edge> edges;

    /**
     * @param source the source vertex the flow was computed for
     * @param sink   the sink vertex the flow was computed for
     * @param value  the total flow value (must be non-negative)
     * @param edges  the edges carrying positive flow (each value is its flow)
     */
    public MaxFlowResult(int source, int sink, int value, List<Edge> edges) {
        if (value < 0) {
            throw new IllegalArgumentException("flow value must be non-negative, was " + value);
        }
        this.source = source;
        this.sink = sink;
        this.value = value;
        this.edges = List.copyOf(edges);
    }

    /** Returns the maximum flow value from source to sink. */
    public int value() {
        return value;
    }

    public int source() {
        return source;
    }

    public int sink() {
        return sink;
    }

    /** Returns the edges carrying positive flow, in row-major order. */
    public List<Edge> flowEdges() {
        return edges;
    }
}
