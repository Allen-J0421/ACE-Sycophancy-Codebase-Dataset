package maxflow;

import java.util.Optional;

final class MaxFlowComputation {
    private final FlowProblem problem;
    private final AugmentingPathFinder pathFinder;
    private final FlowAugmentor augmentor;

    MaxFlowComputation(
        FlowProblem problem,
        AugmentingPathFinder pathFinder,
        FlowAugmentor augmentor
    ) {
        if (problem == null) {
            throw new IllegalArgumentException("problem must not be null");
        }
        if (pathFinder == null) {
            throw new IllegalArgumentException("pathFinder must not be null");
        }
        if (augmentor == null) {
            throw new IllegalArgumentException("augmentor must not be null");
        }

        this.problem = problem;
        this.pathFinder = pathFinder;
        this.augmentor = augmentor;
    }

    MaxFlowResult compute() {
        ResidualGraph residualGraph = problem.network().createResidualGraph();
        int maxFlow = 0;

        Optional<AugmentingPath> path =
            pathFinder.findPath(residualGraph, problem.source(), problem.sink());
        while (path.isPresent()) {
            maxFlow += augmentor.augment(residualGraph, path.get());
            path = pathFinder.findPath(residualGraph, problem.source(), problem.sink());
        }

        return new MaxFlowResult(
            maxFlow,
            problem.source(),
            problem.sink(),
            residualGraph.snapshotCapacities());
    }
}
