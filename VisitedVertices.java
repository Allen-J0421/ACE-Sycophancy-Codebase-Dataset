final class VisitedVertices {

    private final boolean[] visited;

    VisitedVertices(int vertexCount) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("Vertex count must be non-negative.");
        }

        visited = new boolean[vertexCount];
    }

    boolean isVisited(Vertex vertex) {
        return visited[vertex.index()];
    }

    void markVisited(Vertex vertex) {
        visited[vertex.index()] = true;
    }
}
