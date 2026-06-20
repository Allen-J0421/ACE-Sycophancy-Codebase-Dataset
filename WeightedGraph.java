import java.util.List;

public final class WeightedGraph {
    private static final int EDGE_WIDTH = 3;

    private final int vertices;
    private final List<WeightedEdge> edges;

    private WeightedGraph(int vertices, List<WeightedEdge> edges) {
        this.vertices = vertices;
        this.edges = edges;
    }

    public static WeightedGraph from(int vertices, int[][] edgeData) {
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

    public static WeightedGraph of(int vertices, WeightedEdge... edges) {
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
            validateVertex(edge.from(), vertices, "edge start");
            validateVertex(edge.to(), vertices, "edge end");
            normalizedEdges[i] = edge;
        }
        return new WeightedGraph(vertices, List.of(normalizedEdges));
    }

    public int vertices() {
        return vertices;
    }

    public List<WeightedEdge> edges() {
        return edges;
    }

    public int[][] edgeData() {
        int[][] edgeData = new int[edges.size()][3];
        for (int i = 0; i < edges.size(); i++) {
            WeightedEdge edge = edges.get(i);
            edgeData[i][0] = edge.from();
            edgeData[i][1] = edge.to();
            edgeData[i][2] = edge.weight();
        }
        return edgeData;
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
