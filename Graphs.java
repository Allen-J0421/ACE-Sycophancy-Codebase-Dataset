import java.util.ArrayList;
import java.util.List;

final class Graphs {
    private static final int RAW_EDGE_FIELD_COUNT = 3;

    private Graphs() {
    }

    static Graph fromRawEdges(int vertexCount, int[][] rawEdges) {
        validateVertexCount(vertexCount);

        if (rawEdges == null) {
            throw new IllegalArgumentException("Edges cannot be null.");
        }

        List<Edge> parsedEdges = new ArrayList<>(rawEdges.length);
        for (int[] rawEdge : rawEdges) {
            parsedEdges.add(parseEdge(rawEdge, vertexCount));
        }

        return Graph.of(vertexCount, parsedEdges);
    }

    private static Edge parseEdge(int[] rawEdge, int vertexCount) {
        if (rawEdge == null || rawEdge.length != RAW_EDGE_FIELD_COUNT) {
            throw new IllegalArgumentException("Each edge must contain exactly 3 integers.");
        }

        int from = rawEdge[0];
        int to = rawEdge[1];
        validateVertex(from, vertexCount);
        validateVertex(to, vertexCount);
        return new Edge(from, to, rawEdge[2]);
    }

    private static void validateVertexCount(int vertexCount) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("Vertex count cannot be negative.");
        }
    }

    private static void validateVertex(int vertex, int vertexCount) {
        if (vertex < 0 || vertex >= vertexCount) {
            throw new IllegalArgumentException("Vertex index out of bounds: " + vertex);
        }
    }
}
