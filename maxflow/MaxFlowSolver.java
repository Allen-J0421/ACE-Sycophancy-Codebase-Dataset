package maxflow;

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
}
