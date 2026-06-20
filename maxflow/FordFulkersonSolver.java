package maxflow;

import java.util.Optional;

public final class FordFulkersonSolver implements MaxFlowSolver {
    private final FlowProblem problem;
    private final AugmentingPathFinder augmentingPathFinder;

    public FordFulkersonSolver(FlowProblem problem) {
        this(problem, new BreadthFirstAugmentingPathFinder());
    }

    public FordFulkersonSolver(
        FlowNetwork network,
        int source,
        int sink,
        AugmentingPathFinder augmentingPathFinder
    ) {
        this(FlowProblem.of(network, source, sink), augmentingPathFinder);
    }

    public FordFulkersonSolver(FlowNetwork network, int source, int sink) {
        this(FlowProblem.of(network, source, sink), new BreadthFirstAugmentingPathFinder());
    }

    public FordFulkersonSolver(FlowProblem problem, AugmentingPathFinder augmentingPathFinder) {
        if (problem == null) {
            throw new IllegalArgumentException("problem must not be null");
        }
        if (augmentingPathFinder == null) {
            throw new IllegalArgumentException("augmentingPathFinder must not be null");
        }

        this.problem = problem;
        this.augmentingPathFinder = augmentingPathFinder;
    }

    @Override
    public MaxFlowResult solve() {
        ResidualNetwork residualNetwork = problem.network().createResidualNetwork();
        int maxFlow = 0;

        Optional<AugmentingPath> augmentingPath =
            augmentingPathFinder.findPath(residualNetwork, problem.source(), problem.sink());
        while (augmentingPath.isPresent()) {
            AugmentingPath path = augmentingPath.get();
            maxFlow += residualNetwork.augmentPath(path);
            augmentingPath = augmentingPathFinder.findPath(residualNetwork, problem.source(), problem.sink());
        }

        return new MaxFlowResult(maxFlow, problem.source(), problem.sink(), residualNetwork.snapshotCapacities());
    }
}
