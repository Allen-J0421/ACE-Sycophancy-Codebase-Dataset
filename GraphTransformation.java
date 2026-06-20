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
}
