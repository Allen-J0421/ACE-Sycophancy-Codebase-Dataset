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
}
