import java.util.ArrayList;
import java.util.List;

public abstract class AbstractGraph implements Graph {
    protected final List<List<Integer>> adjacencyList;
    protected final int vertexCount;

    protected AbstractGraph(int vertexCount) {
        if (vertexCount <= 0) {
            throw new GraphException.InvalidGraphConfigurationException("Vertex count must be positive");
        }
        this.vertexCount = vertexCount;
        this.adjacencyList = initializeAdjacencyList(vertexCount);
    }

    private List<List<Integer>> initializeAdjacencyList(int size) {
        List<List<Integer>> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            list.add(new ArrayList<>());
        }
        return list;
    }

    @Override
    public int getVertexCount() {
        return vertexCount;
    }

    @Override
    public boolean isValidVertex(int vertex) {
        return vertex >= 0 && vertex < vertexCount;
    }

    protected void validateVertex(int vertex) {
        if (!isValidVertex(vertex)) {
            throw new GraphException.InvalidVertexException(vertex, vertexCount);
        }
    }

    protected void validateEdge(int u, int v) {
        validateVertex(u);
        validateVertex(v);
    }
}
