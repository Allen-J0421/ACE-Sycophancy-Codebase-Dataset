import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class GraphBuilder {
    private final List<List<Edge>> adjacency;

    private GraphBuilder(int vertexCount) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("vertexCount must be non-negative");
        }

        this.adjacency = new ArrayList<>(vertexCount);
        for (int i = 0; i < vertexCount; i++) {
            adjacency.add(new ArrayList<>());
        }
    }

    static GraphBuilder withVertexCount(int vertexCount) {
        return new GraphBuilder(vertexCount);
    }

    GraphBuilder addUndirectedEdge(int from, int to, int weight) {
        addDirectedEdge(from, to, weight);
        addDirectedEdge(to, from, weight);
        return this;
    }

    GraphBuilder addDirectedEdge(int from, int to, int weight) {
        validateVertex(from);
        validateVertex(to);
        validateWeight(weight);
        adjacency.get(from).add(new Edge(to, weight));
        return this;
    }

    Graph build() {
        List<List<Edge>> frozen = new ArrayList<>(adjacency.size());
        for (List<Edge> neighbors : adjacency) {
            frozen.add(Collections.unmodifiableList(new ArrayList<>(neighbors)));
        }
        return new Graph(Collections.unmodifiableList(frozen));
    }

    private void validateVertex(int vertex) {
        if (vertex < 0 || vertex >= adjacency.size()) {
            throw new IndexOutOfBoundsException("vertex out of range: " + vertex);
        }
    }

    private static void validateWeight(int weight) {
        if (weight < 0) {
            throw new IllegalArgumentException("ShortestPath requires non-negative edge weights");
        }
    }
}
