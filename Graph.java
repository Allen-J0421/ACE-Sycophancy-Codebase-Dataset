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
        adjacencyList.get(u).add(v);
        adjacencyList.get(v).add(u);
    }

    int vertexCount() {
        return vertexCount;
    }

    List<Integer> neighbors(int vertex) {
        return Collections.unmodifiableList(adjacencyList.get(vertex));
    }
}
