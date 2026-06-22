import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Graph {
    private final int vertexCount;
    private final List<List<Integer>> adjacencyList;

    Graph(int vertexCount) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("vertexCount must be non-negative");
        }
        this.vertexCount = vertexCount;
        adjacencyList = new ArrayList<>();
        for (int i = 0; i < vertexCount; i++) {
            adjacencyList.add(new ArrayList<>());
        }
    }

    void addEdge(int u, int v) {
        validateVertex(u);
        validateVertex(v);
        if (u == v) {
            throw new IllegalArgumentException("Self-loops are not supported");
        }
        adjacencyList.get(u).add(v);
        adjacencyList.get(v).add(u);
    }

    List<Integer> neighborsOf(int vertex) {
        validateVertex(vertex);
        return Collections.unmodifiableList(adjacencyList.get(vertex));
    }

    private void validateVertex(int vertex) {
        if (vertex < 0 || vertex >= vertexCount) {
            throw new IllegalArgumentException(
                "Vertex must be in range [0, " + (vertexCount - 1) + "]"
            );
        }
    }

    int vertexCount() {
        return vertexCount;
    }
}
