import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

final class GraphValidator {

    private GraphValidator() {
    }

    static void validate(Graph graph) {
        Objects.requireNonNull(graph, "graph");

        int vertexCount = graph.vertexCount();
        validateVertexCount(vertexCount);

        List<Vertex> vertices = Objects.requireNonNull(graph.vertices(), "graph vertices");
        if (vertices.size() != vertexCount) {
            throw new IllegalArgumentException(
                    "Graph vertex list size does not match vertex count: " + vertices.size());
        }

        validateVertices(vertices, vertexCount);
        validateNeighbors(graph, vertices, vertexCount);
    }

    private static void validateVertexCount(int vertexCount) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("Vertex count must be non-negative.");
        }
    }

    private static void validateVertices(List<Vertex> vertices, int vertexCount) {
        Set<Integer> seenIndexes = new HashSet<>();

        for (Vertex vertex : vertices) {
            validateVertex(vertex, vertexCount, "Graph vertex");
            if (!seenIndexes.add(vertex.index())) {
                throw new IllegalArgumentException("Duplicate vertex index: " + vertex.index());
            }
        }

        for (int expectedIndex = 0; expectedIndex < vertexCount; expectedIndex++) {
            if (!seenIndexes.contains(expectedIndex)) {
                throw new IllegalArgumentException("Missing vertex index: " + expectedIndex);
            }
        }
    }

    private static void validateNeighbors(Graph graph, List<Vertex> vertices, int vertexCount) {
        for (Vertex vertex : vertices) {
            List<Vertex> neighbors =
                    Objects.requireNonNull(graph.neighborsOf(vertex), "neighbors for vertex " + vertex);

            for (Vertex neighbor : neighbors) {
                validateVertex(neighbor, vertexCount, "Neighbor vertex");
            }
        }
    }

    private static void validateVertex(Vertex vertex, int vertexCount, String label) {
        if (vertex == null) {
            throw new IllegalArgumentException(label + " must not be null.");
        }
        if (vertex.index() < 0 || vertex.index() >= vertexCount) {
            throw new IllegalArgumentException(label + " index out of bounds: " + vertex.index());
        }
    }
}
