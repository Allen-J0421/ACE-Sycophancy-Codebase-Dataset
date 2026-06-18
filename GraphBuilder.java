public class GraphBuilder {
    private final int vertexCount;
    private final Graph graph;

    public GraphBuilder(int vertexCount) {
        this.vertexCount = vertexCount;
        this.graph = new Graph(vertexCount);
    }

    public GraphBuilder addEdge(int u, int v) {
        graph.addEdge(u, v);
        return this;
    }

    public Graph build() {
        return graph;
    }
}
