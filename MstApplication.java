final class MstApplication {
    private final AdjacencyMatrixSource adjacencyMatrixSource;
    private final GraphFactory graphFactory;
    private final MinimumSpanningTreeAlgorithm algorithm;
    private final MstResultFormatter formatter;

    MstApplication(
            AdjacencyMatrixSource adjacencyMatrixSource,
            GraphFactory graphFactory,
            MinimumSpanningTreeAlgorithm algorithm,
            MstResultFormatter formatter) {
        this.adjacencyMatrixSource = adjacencyMatrixSource;
        this.graphFactory = graphFactory;
        this.algorithm = algorithm;
        this.formatter = formatter;
    }

    String run() {
        AdjacencyMatrix adjacencyMatrix = adjacencyMatrixSource.load();
        Graph graph = graphFactory.create(adjacencyMatrix);
        MstResult result = algorithm.compute(graph);
        return formatter.format(result);
    }
}
