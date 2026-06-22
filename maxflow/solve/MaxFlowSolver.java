package maxflow.solve;

import maxflow.graph.FlowNetwork;

/**
 * Computes the maximum flow from a source vertex to a sink vertex in a
 * {@link FlowNetwork}.
 */
public interface MaxFlowSolver {

    /**
     * Solves the maximum-flow problem.
     *
     * @throws IllegalArgumentException if the source or sink is not a vertex of the
     *         network, or if they are equal
     */
    MaxFlowResult solve(FlowNetwork network, int source, int sink);

    /**
     * Solves the maximum-flow problem, reporting progress to {@code listener}. The
     * default implementation ignores the listener; solvers that support instrumentation
     * (such as {@link FordFulkersonSolver}) override it.
     */
    default MaxFlowResult solve(FlowNetwork network, int source, int sink, SolveListener listener) {
        return solve(network, source, sink);
    }
}
