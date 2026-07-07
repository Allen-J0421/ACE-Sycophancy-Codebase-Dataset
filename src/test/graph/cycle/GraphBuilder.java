package graph.cycle;

class GraphBuilder {
    private final DirectedGraph graph;

    private GraphBuilder(int vertices) {
        graph = new DirectedGraph(vertices);
    }

    static GraphBuilder withVertices(int n) {
        return new GraphBuilder(n);
    }

    GraphBuilder edge(int u, int v) {
        graph.addEdge(u, v);
        return this;
    }

    DirectedGraph build() {
        return graph;
    }
}
