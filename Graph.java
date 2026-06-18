import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Graph {
    private final List<List<Integer>> adjacencyList;
    private final int vertexCount;

    public Graph(int vertexCount) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("Vertex count cannot be negative");
        }
        this.vertexCount = vertexCount;
        this.adjacencyList = new ArrayList<>();
        for (int i = 0; i < vertexCount; i++) {
            adjacencyList.add(new ArrayList<>());
        }
    }

    public void addEdge(int u, int v) {
        validateVertexIndex(u);
        validateVertexIndex(v);
        if (u == v) {
            throw new IllegalArgumentException("Self-loops are not allowed");
        }
        adjacencyList.get(u).add(v);
        adjacencyList.get(v).add(u);
    }

    public List<Integer> getNeighbors(int vertex) {
        validateVertexIndex(vertex);
        return Collections.unmodifiableList(adjacencyList.get(vertex));
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public int getEdgeCount() {
        return adjacencyList.stream()
                .mapToInt(List::size)
                .sum() / 2;
    }

    public boolean hasEdge(int u, int v) {
        validateVertexIndex(u);
        validateVertexIndex(v);
        return adjacencyList.get(u).contains(v);
    }

    public int getDegree(int vertex) {
        validateVertexIndex(vertex);
        return adjacencyList.get(vertex).size();
    }

    private void validateVertexIndex(int vertex) {
        if (vertex < 0 || vertex >= vertexCount) {
            throw new IndexOutOfBoundsException(
                    String.format("Vertex %d is out of bounds [0, %d)", vertex, vertexCount));
        }
    }
}
