final class GraphValidation {
    private GraphValidation() {
    }

    static void requireEdge(DirectedEdge edge) {
        if (edge == null) {
            throw new IllegalArgumentException("Edge cannot be null.");
        }
    }

    static void requireVertex(int vertex, int vertexCount) {
        if (vertex < 0 || vertex >= vertexCount) {
            throw new IllegalArgumentException("Vertex out of bounds: " + vertex);
        }
    }

    static void requireVertexCount(int vertexCount) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("Vertex count must be non-negative.");
        }
    }
}
