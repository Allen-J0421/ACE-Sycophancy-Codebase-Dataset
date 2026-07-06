import java.util.Optional;

class BipartiteChecker {

    private final ColoringStrategy strategy;

    BipartiteChecker(ColoringStrategy strategy) {
        this.strategy = strategy;
    }

    Optional<Partition> check(Graph graph) {
        int V = graph.vertexCount();
        PartitionState state = new PartitionState(V);

        for (int i = 0; i < V; i++) {
            if (state.isUncolored(i)) {
                if (!strategy.colorComponent(graph, i, state)) {
                    return Optional.empty();
                }
            }
        }

        return Optional.of(state.buildPartition());
    }
}
