import java.util.List;

public final class DirectedGraph implements DirectedGraphView {
    private final List<List<Integer>> adjacencyList;

    DirectedGraph(List<List<Integer>> adjacencyList) {
        this.adjacencyList = adjacencyList;
    }

    public int vertexCount() {
        return adjacencyList.size();
    }

    public List<Integer> neighborsOf(int vertex) {
        GraphSupport.validateVertexIndex(vertex, vertexCount());
        return adjacencyList.get(vertex);
    }
}
