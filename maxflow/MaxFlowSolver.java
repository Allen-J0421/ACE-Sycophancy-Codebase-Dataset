package maxflow;

public interface MaxFlowSolver {
    MaxFlowResult solve();

    default int computeMaxFlow() {
        return solve().value();
    }
}
