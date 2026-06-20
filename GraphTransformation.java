import java.util.Objects;

interface GraphTransformation {
    default Graph transform(Graph graph) {
        Graph nonNullGraph = Objects.requireNonNull(graph, "graph");
        validate(nonNullGraph);
        return compute(nonNullGraph);
    }

    default void validate(Graph graph) {
    }

    Graph compute(Graph graph);

    default GraphTransformation andThen(GraphTransformation nextTransformation) {
        GraphTransformation currentTransformation = this;
        GraphTransformation nonNullNextTransformation = Objects.requireNonNull(nextTransformation, "nextTransformation");

        return new GraphTransformation() {
            @Override
            public Graph compute(Graph graph) {
                return nonNullNextTransformation.transform(currentTransformation.transform(graph));
            }
        };
    }

    default GraphTransformation compose(GraphTransformation previousTransformation) {
        GraphTransformation nonNullPreviousTransformation = Objects.requireNonNull(previousTransformation, "previousTransformation");
        return nonNullPreviousTransformation.andThen(this);
    }

    default <Result> GraphAnalysis<Result> thenAnalyze(GraphAnalysis<Result> analysis) {
        GraphTransformation currentTransformation = this;
        GraphAnalysis<Result> nonNullAnalysis = Objects.requireNonNull(analysis, "analysis");

        return new GraphAnalysis<Result>() {
            @Override
            public Result compute(Graph graph) {
                return nonNullAnalysis.analyze(currentTransformation.transform(graph));
            }
        };
    }
}
