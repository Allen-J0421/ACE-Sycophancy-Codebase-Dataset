import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Graph {
    private final int vertexCount;
    private final List<List<Integer>> adjacencyList;

    Graph(int vertexCount) {
        this.vertexCount = vertexCount;
        adjacencyList = new ArrayList<>();
        for (int i = 0; i < vertexCount; i++) {
            adjacencyList.add(new ArrayList<>());
        }
    }

    void addEdge(int u, int v) {
        if (u < 0 || u >= vertexCount || v < 0 || v >= vertexCount) {
            throw new IllegalArgumentException(
                "Vertex indices must be in range [0, " + (vertexCount - 1) + "]"
            );
        }
        adjacencyList.get(u).add(v);
        adjacencyList.get(v).add(u);
    }

    List<Integer> neighborsOf(int vertex) {
        return Collections.unmodifiableList(adjacencyList.get(vertex));
    }

    int vertexCount() {
        return vertexCount;
    }
}
