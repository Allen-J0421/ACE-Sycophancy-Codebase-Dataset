import java.util.Objects;

class GraphBuilder {
    private final Graph graph;

    private GraphBuilder(int vertexCount) {
        this.graph = Graph.create(vertexCount);
    }

    static GraphBuilder withVertexCount(int vertexCount) {
        return new GraphBuilder(vertexCount);
    }

    GraphBuilder addEdge(int source, int destination, int weight) {
        Objects.requireNonNull(graph).addEdge(source, destination, weight);
        return this;
    }

    Graph build() {
        return graph;
    }

    @Override
    public String toString() {
        return String.format("GraphBuilder(%s)", graph);
    }
}
