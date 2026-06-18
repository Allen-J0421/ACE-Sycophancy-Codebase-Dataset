import java.util.List;

final class GraphBuilder {
    private final List<List<Edge>> adjacency;

    private GraphBuilder(int vertexCount) {
        this.adjacency = Graph.emptyAdjacency(vertexCount);
    }

    static GraphBuilder withVertexCount(int vertexCount) {
        return new GraphBuilder(vertexCount);
    }

    GraphBuilder addUndirectedEdge(int from, int to, int weight) {
        addDirectedEdge(from, to, weight);
        addDirectedEdge(to, from, weight);
        return this;
    }

    GraphBuilder addDirectedEdge(int from, int to, int weight) {
        validateVertex(from);
        validateVertex(to);
        validateWeight(weight);
        adjacency.get(from).add(new Edge(to, weight));
        return this;
    }

    Graph build() {
        return new Graph(Graph.freezeAdjacency(adjacency));
    }

    private void validateVertex(int vertex) {
        if (vertex < 0 || vertex >= adjacency.size()) {
            throw new IndexOutOfBoundsException("vertex out of range: " + vertex);
        }
    }

    private static void validateWeight(int weight) {
        if (weight < 0) {
            throw new IllegalArgumentException("Dijkstra requires non-negative edge weights");
        }
    }
}
