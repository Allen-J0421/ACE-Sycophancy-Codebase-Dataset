record Edge(int from, int to, int weight) {
    boolean isWithinVertexRange(int vertexCount) {
        return from >= 0
            && from < vertexCount
            && to >= 0
            && to < vertexCount;
    }

    void validateForVertexCount(int vertexCount) {
        if (!isWithinVertexRange(vertexCount)) {
            throw new IllegalArgumentException(
                "Edge contains vertex out of bounds: (" + from + ", " + to + ")"
            );
        }
    }
}
