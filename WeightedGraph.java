final class WeightedGraph {
    private static final int EDGE_WIDTH = 3;

    private final int vertices;
    private final WeightedEdge[] edges;

    private WeightedGraph(int vertices, WeightedEdge[] edges) {
        this.vertices = vertices;
        this.edges = edges;
    }

    static WeightedGraph from(int vertices, int[][] edgeData) {
        validateVerticesCount(vertices);
        validateEdgeData(edgeData);

        WeightedEdge[] edges = new WeightedEdge[edgeData.length];
        for (int i = 0; i < edgeData.length; i++) {
            int[] edge = edgeData[i];
            if (edge == null || edge.length != EDGE_WIDTH) {
                throw new IllegalArgumentException("each edge must contain exactly 3 integers");
            }
            edges[i] = WeightedEdge.of(edge[0], edge[1], edge[2]);
        }
        return of(vertices, edges);
    }

    static WeightedGraph of(int vertices, WeightedEdge... edges) {
        validateVerticesCount(vertices);
        if (edges == null) {
            throw new IllegalArgumentException("edges must not be null");
        }

        WeightedEdge[] normalizedEdges = new WeightedEdge[edges.length];
        for (int i = 0; i < edges.length; i++) {
            WeightedEdge edge = edges[i];
            if (edge == null) {
                throw new IllegalArgumentException("edges must not contain null entries");
            }
            validateVertex(edge.from, vertices, "edge start");
            validateVertex(edge.to, vertices, "edge end");
            normalizedEdges[i] = edge;
        }
        return new WeightedGraph(vertices, normalizedEdges);
    }

    int vertices() {
        return vertices;
    }

    WeightedEdge[] edgeArray() {
        return edges;
    }

    private static void validateVerticesCount(int vertices) {
        if (vertices <= 0) {
            throw new IllegalArgumentException("vertices must be positive");
        }
    }

    private static void validateEdgeData(int[][] edgeData) {
        if (edgeData == null) {
            throw new IllegalArgumentException("edges must not be null");
        }
    }

    private static void validateVertex(int vertex, int vertices, String label) {
        if (vertex < 0 || vertex >= vertices) {
            throw new IllegalArgumentException(label + " must be within the vertex range");
        }
    }
}
