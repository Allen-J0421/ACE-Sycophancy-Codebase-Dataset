import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Graph {
    private final List<List<Integer>> adjacencyList;

    private Graph(int vertexCount) {
        adjacencyList = new ArrayList<>(vertexCount);
        for (int vertex = 0; vertex < vertexCount; vertex++) {
            adjacencyList.add(new ArrayList<>());
        }
    }

    public static Graph create(int vertexCount) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("vertexCount must be non-negative: " + vertexCount);
        }
        return new Graph(vertexCount);
    }

    public int vertexCount() {
        return adjacencyList.size();
    }

    public void addUndirectedEdge(int u, int v) {
        validateVertex(u);
        validateVertex(v);

        adjacencyList.get(u).add(v);
        adjacencyList.get(v).add(u);
    }

    public void addDirectedEdge(int from, int to) {
        validateVertex(from);
        validateVertex(to);

        adjacencyList.get(from).add(to);
    }

    public List<Integer> neighbors(int vertex) {
        validateVertex(vertex);
        return Collections.unmodifiableList(adjacencyList.get(vertex));
    }

    List<Integer> adjacentVertices(int vertex) {
        return adjacencyList.get(vertex);
    }

    void validateVertex(int vertex) {
        if (vertex < 0 || vertex >= adjacencyList.size()) {
            throw new IllegalArgumentException("vertex out of bounds: " + vertex);
        }
    }
}
