import java.util.ArrayList;
import java.util.List;

final class Graph {
    private final List<List<Edge>> adjacency;

    private Graph(List<List<Edge>> adjacency) {
        this.adjacency = adjacency;
    }

    static Graph withVertexCount(int vertexCount) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("vertexCount must be non-negative");
        }

        List<List<Edge>> adjacency = new ArrayList<>(vertexCount);
        for (int i = 0; i < vertexCount; i++) {
            adjacency.add(new ArrayList<>());
        }
        return new Graph(adjacency);
    }

    void addUndirectedEdge(int from, int to, int weight) {
        addDirectedEdge(from, to, weight);
        addDirectedEdge(to, from, weight);
    }

    void addDirectedEdge(int from, int to, int weight) {
        validateVertex(from);
        validateVertex(to);
        validateWeight(weight);
        adjacency.get(from).add(new Edge(to, weight));
    }

    List<Edge> neighborsOf(int vertex) {
        validateVertex(vertex);
        return adjacency.get(vertex);
    }

    int vertexCount() {
        return adjacency.size();
    }

    private void validateVertex(int vertex) {
        if (vertex < 0 || vertex >= adjacency.size()) {
            throw new IndexOutOfBoundsException("vertex out of range: " + vertex);
        }
    }

    private static void validateWeight(int weight) {
        if (weight < 0) {
            throw new IllegalArgumentException("Dijkstra requires non-negative edge weights");
        }
    }
}
