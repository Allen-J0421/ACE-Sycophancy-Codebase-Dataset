import java.util.List;

record Graph(int vertexCount, List<Edge> edges) {
    Graph {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("Vertex count must be non-negative.");
        }
        if (edges == null) {
            throw new IllegalArgumentException("Edges must not be null.");
        }

        edges = List.copyOf(edges);
        validateEdges(vertexCount, edges);
    }

    static Graph fromEdgeMatrix(int vertexCount, int[][] rawEdges) {
        if (rawEdges == null) {
            throw new IllegalArgumentException("Edges must not be null.");
        }

        return new Graph(vertexCount, java.util.Arrays.stream(rawEdges)
            .map(Edge::fromMatrixRow)
            .toList());
    }

    private static void validateEdges(int vertexCount, List<Edge> edges) {
        for (Edge edge : edges) {
            validateVertex(edge.from(), vertexCount);
            validateVertex(edge.to(), vertexCount);
        }
    }

    private static void validateVertex(int vertex, int vertexCount) {
        if (vertex < 0 || vertex >= vertexCount) {
            throw new IllegalArgumentException(
                "Vertex " + vertex + " is out of bounds for graph with " + vertexCount + " vertices."
            );
        }
    }
}
