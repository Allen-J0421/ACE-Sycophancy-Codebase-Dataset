import java.util.Objects;

interface GraphAnalysis<Result> {
    default Result analyze(Graph graph) {
        Graph nonNullGraph = Objects.requireNonNull(graph, "graph");
        validate(nonNullGraph);
        return compute(nonNullGraph);
    }

    default void validate(Graph graph) {
    }

    Result compute(Graph graph);
}
