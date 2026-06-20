import java.util.ArrayList;
import java.util.List;

public final class DirectedGraphBuilder {
    private final List<List<Integer>> adjacencyList;

    public DirectedGraphBuilder(int vertexCount) {
        GraphSupport.validateVertexCount(vertexCount);

        adjacencyList = new ArrayList<>(vertexCount);
        for (int vertex = 0; vertex < vertexCount; vertex++) {
            adjacencyList.add(new ArrayList<>());
        }
    }

    public DirectedGraphBuilder addEdge(int from, int to) {
        validateVertex(from);
        validateVertex(to);
        adjacencyList.get(from).add(to);
        return this;
    }

    public DirectedGraph build() {
        return new DirectedGraph(GraphSupport.freezeAdjacencyList(adjacencyList));
    }

    private void validateVertex(int vertex) {
        GraphSupport.validateVertexIndex(vertex, adjacencyList.size());
    }
}
