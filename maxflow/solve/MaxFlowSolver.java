package maxflow.solve;

/**
 * Computes the maximum flow for a {@link MaxFlowProblem}.
 */
public interface MaxFlowSolver {

    /** Solves the given maximum-flow problem. */
    MaxFlowResult solve(MaxFlowProblem problem);

    /**
     * Solves the problem, reporting progress to {@code listener}. The default
     * implementation ignores the listener; solvers that support instrumentation (such as
     * {@link FordFulkersonSolver}) override it.
     */
    default MaxFlowResult solve(MaxFlowProblem problem, SolveListener listener) {
        return solve(problem);
    }
}
