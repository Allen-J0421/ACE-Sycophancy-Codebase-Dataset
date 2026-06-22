record Edge(int from, int to, int weight) {
    private static final int RAW_EDGE_FIELD_COUNT = 3;

    static Edge fromRaw(int[] rawEdge, int vertexCount) {
        if (rawEdge == null || rawEdge.length != RAW_EDGE_FIELD_COUNT) {
            throw new IllegalArgumentException("Each edge must contain exactly 3 integers.");
        }

        int from = rawEdge[0];
        int to = rawEdge[1];
        validateVertex(from, vertexCount);
        validateVertex(to, vertexCount);

        return new Edge(from, to, rawEdge[2]);
    }

    private static void validateVertex(int vertex, int vertexCount) {
        if (vertex < 0 || vertex >= vertexCount) {
            throw new IllegalArgumentException("Vertex index out of bounds: " + vertex);
        }
    }
}
