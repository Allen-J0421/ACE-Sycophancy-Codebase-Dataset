final class MstApplication {
    private final GraphSource graphSource;
    private final MinimumSpanningTreeAlgorithm algorithm;
    private final MstResultFormatter formatter;

    MstApplication(
            GraphSource graphSource,
            MinimumSpanningTreeAlgorithm algorithm,
            MstResultFormatter formatter) {
        this.graphSource = graphSource;
        this.algorithm = algorithm;
        this.formatter = formatter;
    }

    String run() {
        Graph graph = graphSource.load();
        MstResult result = algorithm.compute(graph);
        return formatter.format(result);
    }
}
