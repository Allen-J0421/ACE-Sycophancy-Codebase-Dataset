package mst;

import java.util.List;

public record Graph(int vertexCount, List<Edge> edges) {
    public static Graph of(int vertexCount, Edge... edges) {
        if (edges == null) {
            throw new IllegalArgumentException("Edges must not be null.");
        }
        return new Graph(vertexCount, List.of(edges));
    }

    public Graph {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("Vertex count must be non-negative.");
        }
        if (edges == null) {
            throw new IllegalArgumentException("Edges must not be null.");
        }

        edges = List.copyOf(edges);
        validateEdges(vertexCount, edges);
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
