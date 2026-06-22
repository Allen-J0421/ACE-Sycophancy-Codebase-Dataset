package maxflow.solve;

import java.util.List;

/**
 * The outcome of a maximum-flow computation: the total flow value, the source and
 * sink it was computed for, and the flow routed along each original edge.
 *
 * <p>A pure, immutable value object; a {@link MaxFlowSolver} populates it. It has
 * no knowledge of the residual graph it was derived from.
 */
public final class MaxFlowResult {

    /** A single edge carrying a positive amount of flow. */
    public record FlowEdge(int from, int to, int flow) {
        @Override
        public String toString() {
            return from + " -> " + to + ": " + flow;
        }
    }

    private final int source;
    private final int sink;
    private final int value;
    private final List<FlowEdge> edges;

    /**
     * @param source the source vertex the flow was computed for
     * @param sink   the sink vertex the flow was computed for
     * @param value  the total flow value (must be non-negative)
     * @param edges  the edges carrying positive flow
     */
    public MaxFlowResult(int source, int sink, int value, List<FlowEdge> edges) {
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
    public List<FlowEdge> flowEdges() {
        return edges;
    }
}
