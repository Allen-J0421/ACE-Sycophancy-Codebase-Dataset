final class VertexTraversalState {
    private final Vertex source;
    private final boolean[] visited;
    private final Vertex[] parents;

    VertexTraversalState(int size, Vertex source) {
        this.source = source;
        this.visited = new boolean[size];
        this.parents = new Vertex[size];
        this.visited[source.index()] = true;
    }

    boolean hasVisited(Vertex vertex) {
        return visited[vertex.index()];
    }

    void visit(Vertex vertex, Vertex parent) {
        parents[vertex.index()] = parent;
        visited[vertex.index()] = true;
    }

    Vertex parentOf(Vertex vertex) {
        return parents[vertex.index()];
    }

    AugmentingPath createPath(ResidualNetwork network, Vertex sink) {
        int bottleneck = Integer.MAX_VALUE;

        for (Vertex vertex = sink; !vertex.equals(source); vertex = parentOf(vertex)) {
            Vertex previous = parentOf(vertex);
            bottleneck = Math.min(bottleneck, network.capacity(previous, vertex));
        }

        return new AugmentingPath(source, sink, bottleneck, parents);
    }
}
