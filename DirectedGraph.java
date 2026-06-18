import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class DirectedGraph implements Graph {
    private final List<List<Integer>> adjacencyList;
    private final int vertexCount;

    public DirectedGraph(int vertexCount) {
        if (vertexCount <= 0) {
            throw new IllegalArgumentException("Vertex count must be positive");
        }
        this.vertexCount = vertexCount;
        this.adjacencyList = new ArrayList<>();
        for (int i = 0; i < vertexCount; i++) {
            adjacencyList.add(new ArrayList<>());
        }
    }

    @Override
    public void addEdge(int u, int v) {
        if (!isValidVertex(u) || !isValidVertex(v)) {
            throw new IllegalArgumentException("Invalid vertex: u=" + u + ", v=" + v);
        }
        adjacencyList.get(u).add(v);
    }

    @Override
    public List<Integer> getAdjacent(int vertex) {
        if (!isValidVertex(vertex)) {
            throw new IllegalArgumentException("Invalid vertex: " + vertex);
        }
        return Collections.unmodifiableList(adjacencyList.get(vertex));
    }

    @Override
    public int getVertexCount() {
        return vertexCount;
    }

    @Override
    public boolean isValidVertex(int vertex) {
        return vertex >= 0 && vertex < vertexCount;
    }
}
