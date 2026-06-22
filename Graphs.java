import java.util.ArrayList;
import java.util.List;

final class Graphs {
    private static final int RAW_EDGE_FIELD_COUNT = 3;

    private Graphs() {
    }

    static Graph fromRawEdges(int vertexCount, int[][] rawEdges) {
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

        return new Edge(rawEdge[0], rawEdge[1], rawEdge[2]);
    }
}
