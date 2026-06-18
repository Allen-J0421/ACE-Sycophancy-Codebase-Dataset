import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Graph {
    private final int vertexCount;
    private final List<List<Integer>> adjacencyList;

    Graph(int vertexCount) {
        this.vertexCount = vertexCount;
        adjacencyList = new ArrayList<>(vertexCount);
        for (int i = 0; i < vertexCount; i++) {
            adjacencyList.add(new ArrayList<>());
        }
    }

    void addEdge(int u, int v) {
        validateVertex(u);
        validateVertex(v);
        adjacencyList.get(u).add(v);
        adjacencyList.get(v).add(u);
    }

    int vertexCount() {
        return vertexCount;
    }

    List<Integer> neighbors(int vertex) {
        validateVertex(vertex);
        return Collections.unmodifiableList(adjacencyList.get(vertex));
    }

    private void validateVertex(int vertex) {
        if (vertex < 0 || vertex >= vertexCount) {
            throw new IllegalArgumentException(
                "Vertex " + vertex + " is out of range [0, " + (vertexCount - 1) + "]");
        }
    }
}
