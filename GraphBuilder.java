public class GraphBuilder {
    private final Graph graph;

    private GraphBuilder(Graph graph) {
        this.graph = graph;
    }

    public static GraphBuilder undirected(int vertexCount) {
        return new GraphBuilder(new UndirectedGraph(vertexCount));
    }

    public static GraphBuilder directed(int vertexCount) {
        return new GraphBuilder(new DirectedGraph(vertexCount));
    }

    public GraphBuilder addEdge(int u, int v) {
        graph.addEdge(u, v);
        return this;
    }

    public Graph build() {
        return graph;
    }
}
