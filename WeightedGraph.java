import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class WeightedGraph {
    private final List<List<Edge>> adjacency;

    static final class Builder {
        private final ArrayList<ArrayList<Edge>> adjacency;

        private Builder(int vertexCount) {
            validateVertexCount(vertexCount);

            adjacency = new ArrayList<>();
            for (int i = 0; i < vertexCount; i++) {
                adjacency.add(new ArrayList<>());
            }
        }

        Builder addUndirectedEdge(int u, int v, int weight) {
            addDirectedEdge(u, v, weight);
            addDirectedEdge(v, u, weight);
            return this;
        }

        Builder addDirectedEdge(int from, int to, int weight) {
            validateVertex(from, adjacency.size());
            validateVertex(to, adjacency.size());
            Edge.validateWeight(weight);

            adjacency.get(from).add(new Edge(to, weight));
            return this;
        }

        WeightedGraph build() {
            return new WeightedGraph(freeze(adjacency));
        }
    }

    private WeightedGraph(List<List<Edge>> adjacency) {
        this.adjacency = adjacency;
    }

    static Builder builder(int vertexCount) {
        return new Builder(vertexCount);
    }

    int vertexCount() {
        return adjacency.size();
    }

    boolean hasVertex(int vertex) {
        return vertex >= 0 && vertex < adjacency.size();
    }

    List<Edge> edgesFrom(int vertex) {
        validateVertex(vertex, adjacency.size());
        return adjacency.get(vertex);
    }

    private static List<List<Edge>> freeze(ArrayList<ArrayList<Edge>> adjacency) {
        ArrayList<List<Edge>> frozen = new ArrayList<>();
        for (List<Edge> edges : adjacency) {
            frozen.add(Collections.unmodifiableList(new ArrayList<>(edges)));
        }
        return Collections.unmodifiableList(frozen);
    }

    private static void validateVertexCount(int vertexCount) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("Vertex count cannot be negative.");
        }
    }

    private static void validateVertex(int vertex, int vertexCount) {
        if (vertex < 0 || vertex >= vertexCount) {
            throw new IllegalArgumentException("Vertex is outside the graph.");
        }
    }
}
