import java.util.List;

final class UndirectedGraph implements GraphView {

    private final int vertexCount;
    private final List<List<Integer>> adjacencyList;

    UndirectedGraph(int vertexCount, List<List<Integer>> adjacencyList) {
        this.vertexCount = vertexCount;
        this.adjacencyList = adjacencyList;
    }

    @Override
    public int vertexCount() {
        return vertexCount;
    }

    @Override
    public List<Integer> neighborsOf(int vertex) {
        return adjacencyList.get(vertex);
    }
}
