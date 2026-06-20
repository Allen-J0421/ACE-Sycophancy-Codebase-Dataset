import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class UndirectedGraph {
    private final List<List<Integer>> adjacency;
    private final List<List<Integer>> neighborsView;

    UndirectedGraph(int vertexCount) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("vertexCount must be non-negative");
        }
        adjacency = new ArrayList<>(vertexCount);
        neighborsView = new ArrayList<>(vertexCount);
        for (int vertex = 0; vertex < vertexCount; vertex++) {
            List<Integer> neighbors = new ArrayList<>();
            adjacency.add(neighbors);
            neighborsView.add(Collections.unmodifiableList(neighbors));
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
        return neighborsView.get(vertex);
    }

    private void validateVertex(int vertex) {
        if (vertex < 0 || vertex >= adjacency.size()) {
            throw new IllegalArgumentException("Vertex out of range: " + vertex);
        }
    }
}
