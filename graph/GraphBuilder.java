package graph;

public final class GraphBuilder extends AbstractAdjacencyGraph {
    private final AdjacencyLists adjacencyLists;

    private GraphBuilder(AdjacencyLists adjacencyLists) {
        super(adjacencyLists.views());
        this.adjacencyLists = adjacencyLists;
    }

    public static GraphBuilder withVertices(int vertexCount) {
        return new GraphBuilder(AdjacencyLists.withVertices(vertexCount));
    }

    public GraphBuilder addDirectedEdge(int from, int to) {
        requireVertex(from);
        requireVertex(to);
        adjacencyLists.addDirectedEdge(from, to);
        return this;
    }

    public GraphBuilder addUndirectedEdge(int from, int to) {
        return addDirectedEdge(from, to).addDirectedEdge(to, from);
    }

    public Graph build() {
        return new AdjacencyListGraph(adjacencyLists.snapshotViews());
    }
}
