import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class Graph {
    private final List<List<Integer>> adjacency;

    Graph(int vertexCount) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("vertexCount must be non-negative");
        }
        adjacency = new ArrayList<>(vertexCount);
        for (int vertex = 0; vertex < vertexCount; vertex++) {
            adjacency.add(new ArrayList<>());
        }
    }

    int size() {
        return adjacency.size();
    }

    void addUndirectedEdge(int u, int v) {
        validateVertex(u);
        validateVertex(v);
        adjacency.get(u).add(v);
        adjacency.get(v).add(u);
    }

    List<Integer> neighborsOf(int vertex) {
        validateVertex(vertex);
        return Collections.unmodifiableList(adjacency.get(vertex));
    }

    private void validateVertex(int vertex) {
        if (vertex < 0 || vertex >= adjacency.size()) {
            throw new IllegalArgumentException("Vertex out of range: " + vertex);
        }
    }
}
