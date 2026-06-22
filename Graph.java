import java.util.List;

record Graph(int vertexCount, List<Edge> edges) {
    static Graph of(int vertexCount, List<Edge> edges) {
        return new Graph(vertexCount, edges);
    }

    Graph {
        validateVertexCount(vertexCount);

        if (edges == null) {
            throw new IllegalArgumentException("Edges cannot be null.");
        }

        edges = List.copyOf(edges);
        validateEdges(edges, vertexCount);
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

    private static void validateEdges(List<Edge> edges, int vertexCount) {
        for (Edge edge : edges) {
            if (edge == null) {
                throw new IllegalArgumentException("Edges cannot contain null values.");
            }

            edge.validateForVertexCount(vertexCount);
        }
    }
}
