import java.util.ArrayList;
import java.util.List;

public class Graph {
    private final List<List<Integer>> adjacencyList;
    private final int vertexCount;

    public Graph(int vertexCount) {
        this.vertexCount = vertexCount;
        this.adjacencyList = new ArrayList<>();
        for (int i = 0; i < vertexCount; i++) {
            adjacencyList.add(new ArrayList<>());
        }
    }

    public void addEdge(int u, int v) {
        adjacencyList.get(u).add(v);
        adjacencyList.get(v).add(u);
    }

    public List<Integer> getNeighbors(int vertex) {
        return adjacencyList.get(vertex);
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public List<List<Integer>> getAdjacencyList() {
        return adjacencyList;
    }
}
