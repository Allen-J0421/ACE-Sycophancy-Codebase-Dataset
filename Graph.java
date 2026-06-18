import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Graph {
    private final List<List<Integer>> adjacencyList;
    private final int vertexCount;
    private int edgeCount;

    public Graph(int vertexCount) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("Vertex count cannot be negative");
        }
        this.vertexCount = vertexCount;
        this.adjacencyList = new ArrayList<>();
        this.edgeCount = 0;

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
        if (!hasEdge(u, v)) {
            adjacencyList.get(u).add(v);
            adjacencyList.get(v).add(u);
            edgeCount++;
        }
    }

    public void removeEdge(int u, int v) {
        validateVertexIndex(u);
        validateVertexIndex(v);
        if (adjacencyList.get(u).remove(Integer.valueOf(v))) {
            adjacencyList.get(v).remove(Integer.valueOf(u));
            edgeCount--;
        }
    }

    public List<Integer> getNeighbors(int vertex) {
        validateVertexIndex(vertex);
        return Collections.unmodifiableList(adjacencyList.get(vertex));
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public int getEdgeCount() {
        return edgeCount;
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

    public boolean isEmpty() {
        return vertexCount == 0;
    }

    public List<Integer> getAllVertices() {
        List<Integer> vertices = new ArrayList<>();
        for (int i = 0; i < vertexCount; i++) {
            vertices.add(i);
        }
        return vertices;
    }

    public Set<Integer> getVerticesWithDegree(int degree) {
        Set<Integer> vertices = new HashSet<>();
        for (int i = 0; i < vertexCount; i++) {
            if (getDegree(i) == degree) {
                vertices.add(i);
            }
        }
        return vertices;
    }

    private void validateVertexIndex(int vertex) {
        if (vertex < 0 || vertex >= vertexCount) {
            throw new IndexOutOfBoundsException(
                    String.format("Vertex %d is out of bounds [0, %d)", vertex, vertexCount));
        }
    }
}
