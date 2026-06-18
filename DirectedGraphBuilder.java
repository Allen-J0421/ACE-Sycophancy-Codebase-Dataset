import java.util.List;

public final class DirectedGraphBuilder {
    private final List<List<Integer>> adjacencyList;

    DirectedGraphBuilder(int vertexCount) {
        this.adjacencyList = DirectedGraph.createEmptyAdjacencyList(vertexCount);
    }

    public DirectedGraphBuilder addEdge(DirectedEdge edge) {
        GraphValidation.requireEdge(edge);
        return addEdge(edge.source(), edge.destination());
    }

    public DirectedGraphBuilder addEdge(int source, int destination) {
        GraphValidation.requireVertex(source, adjacencyList.size());
        GraphValidation.requireVertex(destination, adjacencyList.size());
        adjacencyList.get(source).add(destination);
        return this;
    }

    public DirectedGraph build() {
        return DirectedGraph.fromAdjacencyList(adjacencyList);
    }
}
