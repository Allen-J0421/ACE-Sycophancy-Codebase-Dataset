import java.util.Objects;

final class GraphInputValidator {

    private GraphInputValidator() {
    }

    static void validateVertexCount(int vertexCount) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("vertexCount must be non-negative");
        }
    }

    static void validateEdgeList(int[][] edges) {
        Objects.requireNonNull(edges, "edges");
    }

    static void validateEdge(int[] edge) {
        if (edge == null || edge.length != 2) {
            throw new IllegalArgumentException("Each edge must contain exactly two vertices");
        }
    }

    static void validateVertex(int vertex, int vertexCount) {
        if (vertex < 0 || vertex >= vertexCount) {
            throw new IllegalArgumentException(
                    "Vertex " + vertex + " is out of bounds for graph size " + vertexCount);
        }
    }
}
