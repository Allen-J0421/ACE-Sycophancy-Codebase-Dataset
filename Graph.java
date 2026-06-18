import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class Graph {
    private final List<List<Edge>> adjacency;

    Graph(List<List<Edge>> adjacency) {
        this.adjacency = adjacency;
    }

    List<Edge> neighborsOf(int vertex) {
        if (vertex < 0 || vertex >= adjacency.size()) {
            throw new IndexOutOfBoundsException("vertex out of range: " + vertex);
        }
        return adjacency.get(vertex);
    }

    int vertexCount() {
        return adjacency.size();
    }

    static List<List<Edge>> emptyAdjacency(int vertexCount) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("vertexCount must be non-negative");
        }

        List<List<Edge>> adjacency = new ArrayList<>(vertexCount);
        for (int i = 0; i < vertexCount; i++) {
            adjacency.add(new ArrayList<>());
        }
        return adjacency;
    }

    static List<List<Edge>> freezeAdjacency(List<List<Edge>> adjacency) {
        List<List<Edge>> frozen = new ArrayList<>(adjacency.size());
        for (List<Edge> neighbors : adjacency) {
            frozen.add(Collections.unmodifiableList(new ArrayList<>(neighbors)));
        }
        return Collections.unmodifiableList(frozen);
    }
}
