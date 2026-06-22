package maxflow.solve;

import maxflow.graph.Capacity;
import maxflow.graph.Edge;
import maxflow.graph.FlowNetwork;
import maxflow.graph.ResidualGraph;
import maxflow.path.AugmentingPath;
import maxflow.path.AugmentingPathFinder;
import maxflow.path.BreadthFirstPathFinder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Computes a maximum flow with the Ford-Fulkerson method: repeatedly find an
 * augmenting path in the residual graph and push its bottleneck amount of flow,
 * until no augmenting path remains.
 *
 * <p>The way augmenting paths are discovered is delegated to a pluggable
 * {@link AugmentingPathFinder}, so the same solver can act as Edmonds-Karp (with
 * {@link BreadthFirstPathFinder}, the default) or as the textbook depth-first
 * Ford-Fulkerson method.
 */
public final class FordFulkersonSolver implements MaxFlowSolver {

    private final AugmentingPathFinder pathFinder;

    /** Creates a solver using breadth-first search (Edmonds-Karp). */
    public FordFulkersonSolver() {
        this(new BreadthFirstPathFinder());
    }

    /** Creates a solver using the given augmenting-path strategy. */
    public FordFulkersonSolver(AugmentingPathFinder pathFinder) {
        this.pathFinder = Objects.requireNonNull(pathFinder, "pathFinder");
    }

    @Override
    public MaxFlowResult solve(FlowNetwork network, int source, int sink) {
        validate(network, source, sink);

        ResidualGraph residual = new ResidualGraph(network);
        Capacity maxFlow = Capacity.ZERO;

        Optional<AugmentingPath> path;
        while ((path = pathFinder.findPath(residual, source, sink)).isPresent()) {
            Capacity bottleneck = path.get().bottleneck();
            pushAlong(residual, path.get(), bottleneck);
            maxFlow = maxFlow.plus(bottleneck);
        }

        return new MaxFlowResult(source, sink, maxFlow, extractFlowEdges(network, residual));
    }

    /** Reads the net flow on every original edge out of the final residual graph. */
    private static List<Edge> extractFlowEdges(FlowNetwork network, ResidualGraph residual) {
        List<Edge> flowEdges = new ArrayList<>();
        for (Edge capacityEdge : network.edges()) {
            Capacity flow = residual.flowOn(capacityEdge.from(), capacityEdge.to());
            if (flow.isPositive()) {
                flowEdges.add(capacityEdge.withValue(flow));
            }
        }
        return flowEdges;
    }

    /** Returns the augmenting-path strategy this solver uses. */
    public AugmentingPathFinder pathFinder() {
        return pathFinder;
    }

    private static void pushAlong(ResidualGraph residual, AugmentingPath path, Capacity amount) {
        var vertices = path.vertices();
        for (int i = 0; i + 1 < vertices.size(); i++) {
            residual.pushFlow(vertices.get(i), vertices.get(i + 1), amount);
        }
    }

    private static void validate(FlowNetwork network, int source, int sink) {
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
}
