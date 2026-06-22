import java.util.ArrayList;
import java.util.List;

record Graph(int vertexCount, List<Edge> edges) {
    static Graph fromRawEdges(int vertexCount, int[][] rawEdges) {
        validateVertexCount(vertexCount);

        if (rawEdges == null) {
            throw new IllegalArgumentException("Edges cannot be null.");
        }

        List<Edge> parsedEdges = new ArrayList<>(rawEdges.length);
        for (int[] rawEdge : rawEdges) {
            parsedEdges.add(Edge.fromRaw(rawEdge, vertexCount));
        }

        return new Graph(vertexCount, parsedEdges);
    }

    Graph {
        validateVertexCount(vertexCount);

        if (edges == null) {
            throw new IllegalArgumentException("Edges cannot be null.");
        }

        edges = List.copyOf(edges);
    }

    int requiredEdgeCount() {
        return Math.max(0, vertexCount - 1);
    }

    boolean isTriviallyConnected() {
        return vertexCount <= 1;
    }

    boolean canBeSpannedWith(int edgeCount) {
        return isTriviallyConnected() || edgeCount == requiredEdgeCount();
    }

    void validateSpanningTree(MinimumSpanningTree spanningTree) {
        if (canBeSpannedWith(spanningTree.edgeCount())) {
            return;
        }

        throw new IllegalArgumentException("Input graph must be connected to form an MST.");
    }

    private static void validateVertexCount(int vertexCount) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("Vertex count cannot be negative.");
        }
    }
}
