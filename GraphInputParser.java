import java.util.ArrayList;
import java.util.List;

final class GraphInputParser {
    static final int EDGE_FIELD_COUNT = 3;

    private GraphInputParser() {
    }

    static Graph parse(int vertexCount, int[][] rawEdges) {
        validateVertexCount(vertexCount);

        if (rawEdges == null) {
            throw new IllegalArgumentException("Edges cannot be null.");
        }

        List<Edge> edges = new ArrayList<>(rawEdges.length);
        for (int[] rawEdge : rawEdges) {
            edges.add(Edge.fromRaw(rawEdge, vertexCount));
        }

        return new Graph(vertexCount, edges);
    }

    private static void validateVertexCount(int vertexCount) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("Vertex count cannot be negative.");
        }
    }
}
