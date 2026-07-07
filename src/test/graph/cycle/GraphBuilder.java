package graph.cycle;

class GraphBuilder {
    private final DirectedGraph.Builder builder;

    private GraphBuilder(int vertices) {
        builder = DirectedGraph.builder(vertices);
    }

    static GraphBuilder withVertices(int n) {
        return new GraphBuilder(n);
    }

    GraphBuilder edge(int u, int v) {
        builder.addEdge(u, v);
        return this;
    }

    DirectedGraph build() {
        return builder.build();
    }
}
