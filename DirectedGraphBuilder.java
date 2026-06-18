import java.util.List;

public final class DirectedGraphBuilder {
    private final List<List<Integer>> adjacencyList;

    DirectedGraphBuilder(int vertexCount) {
        this.adjacencyList = DirectedGraph.createEmptyAdjacencyList(vertexCount);
    }

    public DirectedGraphBuilder addEdge(DirectedEdge edge) {
        validateEdge(edge);
        return addEdge(edge.source(), edge.destination());
    }

    public DirectedGraphBuilder addEdge(int source, int destination) {
        validateVertex(source);
        validateVertex(destination);
        adjacencyList.get(source).add(destination);
        return this;
    }

    public DirectedGraph build() {
        return new DirectedGraph(DirectedGraph.freezeAdjacencyList(adjacencyList));
    }

    private void validateEdge(DirectedEdge edge) {
        if (edge == null) {
            throw new IllegalArgumentException("Edge cannot be null.");
        }
    }

    private void validateVertex(int vertex) {
        if (vertex < 0 || vertex >= adjacencyList.size()) {
            throw new IllegalArgumentException("Vertex out of bounds: " + vertex);
        }
    }
}
