import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class Graph {
    private final int vertexCount;
    private final List<Edge> edges;

    Graph(int vertexCount, List<Edge> edges) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("Vertex count must be non-negative.");
        }
        if (edges == null) {
            throw new IllegalArgumentException("Edges must not be null.");
        }

        this.vertexCount = vertexCount;
        this.edges = Collections.unmodifiableList(new ArrayList<>(edges));
        validateEdges();
    }

    static Graph fromEdgeMatrix(int vertexCount, int[][] rawEdges) {
        if (rawEdges == null) {
            throw new IllegalArgumentException("Edges must not be null.");
        }

        List<Edge> edges = new ArrayList<>(rawEdges.length);
        for (int[] row : rawEdges) {
            edges.add(Edge.fromMatrixRow(row));
        }
        return new Graph(vertexCount, edges);
    }

    int vertexCount() {
        return vertexCount;
    }

    List<Edge> edges() {
        return edges;
    }

    private void validateEdges() {
        for (Edge edge : edges) {
            validateVertex(edge.from());
            validateVertex(edge.to());
        }
    }

    private void validateVertex(int vertex) {
        if (vertex < 0 || vertex >= vertexCount) {
            throw new IllegalArgumentException(
                "Vertex " + vertex + " is out of bounds for graph with " + vertexCount + " vertices."
            );
        }
    }
}
