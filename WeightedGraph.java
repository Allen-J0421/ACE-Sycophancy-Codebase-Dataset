import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class WeightedGraph {
    private final ArrayList<ArrayList<Edge>> adjacency;

    private WeightedGraph(int vertexCount) {
        validateVertexCount(vertexCount);

        adjacency = new ArrayList<>();
        for (int i = 0; i < vertexCount; i++) {
            adjacency.add(new ArrayList<>());
        }
    }

    static WeightedGraph withVertexCount(int vertexCount) {
        return new WeightedGraph(vertexCount);
    }

    void addUndirectedEdge(int u, int v, int weight) {
        addDirectedEdge(u, v, weight);
        addDirectedEdge(v, u, weight);
    }

    void addDirectedEdge(int from, int to, int weight) {
        validateVertex(from);
        validateVertex(to);
        Edge.validateWeight(weight);

        adjacency.get(from).add(new Edge(to, weight));
    }

    int vertexCount() {
        return adjacency.size();
    }

    boolean hasVertex(int vertex) {
        return vertex >= 0 && vertex < adjacency.size();
    }

    List<Edge> edgesFrom(int vertex) {
        validateVertex(vertex);
        return Collections.unmodifiableList(adjacency.get(vertex));
    }

    private static void validateVertexCount(int vertexCount) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("Vertex count cannot be negative.");
        }
    }

    private void validateVertex(int vertex) {
        if (!hasVertex(vertex)) {
            throw new IllegalArgumentException("Vertex is outside the graph.");
        }
    }
}
