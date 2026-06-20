package maxflow;

public final class FordFulkersonSolver implements MaxFlowSolver {
    private final FlowProblem problem;
    private final AugmentingPathFinder augmentingPathFinder;
    private final FlowAugmentor augmentor;

    public FordFulkersonSolver(FlowProblem problem) {
        this(problem, new BreadthFirstAugmentingPathFinder(), new ResidualFlowAugmentor());
    }

    public FordFulkersonSolver(
        FlowNetwork network,
        int source,
        int sink,
        AugmentingPathFinder augmentingPathFinder
    ) {
        this(FlowProblem.of(network, source, sink), augmentingPathFinder, new ResidualFlowAugmentor());
    }

    public FordFulkersonSolver(FlowNetwork network, int source, int sink) {
        this(
            FlowProblem.of(network, source, sink),
            new BreadthFirstAugmentingPathFinder(),
            new ResidualFlowAugmentor());
    }

    public FordFulkersonSolver(
        FlowProblem problem,
        AugmentingPathFinder augmentingPathFinder
    ) {
        this(problem, augmentingPathFinder, new ResidualFlowAugmentor());
    }

    FordFulkersonSolver(
        FlowProblem problem,
        AugmentingPathFinder augmentingPathFinder,
        FlowAugmentor augmentor
    ) {
        if (problem == null) {
            throw new IllegalArgumentException("problem must not be null");
        }
        if (augmentingPathFinder == null) {
            throw new IllegalArgumentException("augmentingPathFinder must not be null");
        }
        if (augmentor == null) {
            throw new IllegalArgumentException("augmentor must not be null");
        }

        this.problem = problem;
        this.augmentingPathFinder = augmentingPathFinder;
        this.augmentor = augmentor;
    }

    @Override
    public MaxFlowResult solve() {
        return new MaxFlowComputation(problem, augmentingPathFinder, augmentor).compute();
    }
}
