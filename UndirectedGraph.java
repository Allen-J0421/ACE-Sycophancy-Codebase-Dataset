import java.util.ArrayList;
import java.util.List;

public class UndirectedGraph {
    private final List<List<Integer>> adjacencyList;
    private final int vertexCount;

    public UndirectedGraph(int vertexCount) {
        this.vertexCount = vertexCount;
        this.adjacencyList = new ArrayList<>();
        for (int i = 0; i < vertexCount; i++) {
            adjacencyList.add(new ArrayList<>());
        }
    }

    public void addEdge(int u, int v) {
        if (isValidVertex(u) && isValidVertex(v)) {
            adjacencyList.get(u).add(v);
            adjacencyList.get(v).add(u);
        }
    }

    public List<Integer> getAdjacent(int vertex) {
        return adjacencyList.get(vertex);
    }

    public int getVertexCount() {
        return vertexCount;
    }

    private boolean isValidVertex(int vertex) {
        return vertex >= 0 && vertex < vertexCount;
    }
}
