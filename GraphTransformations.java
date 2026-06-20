import java.util.Objects;
import java.util.function.Predicate;

final class GraphTransformations {
    private GraphTransformations() {
    }

    static GraphTransformation identity() {
        return new GraphTransformation() {
            @Override
            public Graph compute(Graph graph) {
                return graph;
            }
        };
    }

    static GraphTransformation copy() {
        return new GraphTransformation() {
            @Override
            public Graph compute(Graph graph) {
                return GraphBuilder.fromGraph(graph).build();
            }
        };
    }

    static GraphTransformation filterEdges(Predicate<Edge> edgePredicate) {
        Predicate<Edge> nonNullEdgePredicate = Objects.requireNonNull(edgePredicate, "edgePredicate");

        return new GraphTransformation() {
            @Override
            public Graph compute(Graph graph) {
                GraphBuilder builder = Graph.builder(graph.vertexCount(), graph.type());
                for (Edge edge : graph.edges()) {
                    if (nonNullEdgePredicate.test(edge)) {
                        builder.addEdge(edge);
                    }
                }

                return builder.build();
            }
        };
    }

    static GraphTransformation chain(GraphTransformation... transformations) {
        Objects.requireNonNull(transformations, "transformations");

        GraphTransformation chainedTransformation = identity();
        for (GraphTransformation transformation : transformations) {
            chainedTransformation = chainedTransformation.andThen(Objects.requireNonNull(transformation, "transformation"));
        }

        return chainedTransformation;
    }
}
