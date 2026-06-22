package maxflow;

import java.util.ArrayList;
import java.util.List;

/**
 * The outcome of a maximum-flow computation: the total flow value, the source and
 * sink it was computed for, and the flow routed along each original edge.
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

    private MaxFlowResult(int source, int sink, int value, List<FlowEdge> edges) {
        this.source = source;
        this.sink = sink;
        this.value = value;
        this.edges = List.copyOf(edges);
    }

    /**
     * Builds a result by reading the net flow on every original edge of the
     * network out of the final residual graph.
     */
    public static MaxFlowResult from(ResidualGraph residual, int source, int sink, int value) {
        FlowNetwork network = residual.network();
        List<FlowEdge> edges = new ArrayList<>();
        for (int from = 0; from < network.vertexCount(); from++) {
            for (int to = 0; to < network.vertexCount(); to++) {
                if (network.capacity(from, to) > 0) {
                    int flow = residual.flowOn(from, to);
                    if (flow > 0) {
                        edges.add(new FlowEdge(from, to, flow));
                    }
                }
            }
        }
        return new MaxFlowResult(source, sink, value, edges);
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
