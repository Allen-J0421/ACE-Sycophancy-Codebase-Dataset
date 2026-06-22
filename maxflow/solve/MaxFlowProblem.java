package maxflow.solve;

import java.util.Objects;

import maxflow.graph.FlowNetwork;

/**
 * An immutable maximum-flow problem instance: the {@link FlowNetwork} to solve, together
 * with the chosen source and sink. Bundling these into one value object gives a
 * {@link MaxFlowSolver} a single, self-validating input, and keeps the <em>query</em>
 * (which two vertices) cleanly separate from the <em>network</em> (which a problem reuses
 * but never modifies, and which can be posed for any source/sink pair).
 *
 * <p>The source and sink are checked against the network when the problem is constructed,
 * so a solver receives an already-valid input and never has to re-validate it.
 */
public record MaxFlowProblem(FlowNetwork network, int source, int sink) {

    public MaxFlowProblem {
        Objects.requireNonNull(network, "network");
        if (!network.isVertex(source)) {
            throw new IllegalArgumentException("source " + source + " is not a vertex of the network");
        }
        if (!network.isVertex(sink)) {
            throw new IllegalArgumentException("sink " + sink + " is not a vertex of the network");
        }
        if (source == sink) {
            throw new IllegalArgumentException("source and sink must differ (both " + source + ")");
        }
    }

    @Override
    public String toString() {
        return "max-flow problem: source " + source + " -> sink " + sink
                + " over " + network.vertexCount() + " vertices";
    }
}
