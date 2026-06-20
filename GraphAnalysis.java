import java.util.Objects;
import java.util.function.Function;

interface GraphAnalysis<Result> {
    default Result analyze(Graph graph) {
        Graph nonNullGraph = Objects.requireNonNull(graph, "graph");
        validate(nonNullGraph);
        return compute(nonNullGraph);
    }

    default void validate(Graph graph) {
    }

    Result compute(Graph graph);

    default GraphAnalysis<Result> compose(GraphTransformation transformation) {
        GraphAnalysis<Result> currentAnalysis = this;
        GraphTransformation nonNullTransformation = Objects.requireNonNull(transformation, "transformation");

        return new GraphAnalysis<Result>() {
            @Override
            public Result compute(Graph graph) {
                return currentAnalysis.analyze(nonNullTransformation.transform(graph));
            }
        };
    }

    default <MappedResult> GraphAnalysis<MappedResult> map(Function<? super Result, ? extends MappedResult> mapper) {
        GraphAnalysis<Result> currentAnalysis = this;
        Function<? super Result, ? extends MappedResult> nonNullMapper = Objects.requireNonNull(mapper, "mapper");

        return new GraphAnalysis<MappedResult>() {
            @Override
            public MappedResult compute(Graph graph) {
                return nonNullMapper.apply(currentAnalysis.analyze(graph));
            }
        };
    }
}
