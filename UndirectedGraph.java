import java.util.ArrayList;
import java.util.List;

final class UndirectedGraph implements Graph {

    private final List<Vertex> vertices;
    private final List<List<Vertex>> adjacencyList;

    private UndirectedGraph(List<Vertex> vertices, List<List<Vertex>> adjacencyList) {
        this.vertices = vertices;
        this.adjacencyList = adjacencyList;
    }

    static Builder builder(int vertexCount) {
        return new Builder(vertexCount);
    }

    static UndirectedGraph empty(int vertexCount) {
        return builder(vertexCount).build();
    }

    @Override
    public int vertexCount() {
        return adjacencyList.size();
    }

    @Override
    public List<Vertex> vertices() {
        return vertices;
    }

    @Override
    public List<Vertex> neighborsOf(Vertex vertex) {
        validateVertex(vertex);
        return adjacencyList.get(vertex.index());
    }

    private void validateVertex(Vertex vertex) {
        if (vertex == null) {
            throw new IllegalArgumentException("Vertex must not be null.");
        }
        validateVertexIndex(vertex.index());
    }

    private void validateVertexIndex(int vertexIndex) {
        if (vertexIndex < 0 || vertexIndex >= adjacencyList.size()) {
            throw new IllegalArgumentException("Vertex index out of bounds: " + vertexIndex);
        }
    }

    static final class Builder {

        private final List<Vertex> vertices;
        private final List<List<Vertex>> adjacencyList;

        Builder(int vertexCount) {
            if (vertexCount < 0) {
                throw new IllegalArgumentException("Vertex count must be non-negative.");
            }

            vertices = new ArrayList<>(vertexCount);
            adjacencyList = new ArrayList<>(vertexCount);
            for (int vertexIndex = 0; vertexIndex < vertexCount; vertexIndex++) {
                vertices.add(new Vertex(vertexIndex));
                adjacencyList.add(new ArrayList<>());
            }
        }

        Builder addEdge(int fromVertex, int toVertex) {
            validateVertexIndex(fromVertex);
            validateVertexIndex(toVertex);

            adjacencyList.get(fromVertex).add(vertices.get(toVertex));
            adjacencyList.get(toVertex).add(vertices.get(fromVertex));
            return this;
        }

        UndirectedGraph build() {
            List<List<Vertex>> snapshot = new ArrayList<>(adjacencyList.size());
            for (List<Vertex> neighbors : adjacencyList) {
                snapshot.add(List.copyOf(neighbors));
            }

            return new UndirectedGraph(List.copyOf(vertices), List.copyOf(snapshot));
        }

        private void validateVertexIndex(int vertexIndex) {
            if (vertexIndex < 0 || vertexIndex >= adjacencyList.size()) {
                throw new IllegalArgumentException("Vertex index out of bounds: " + vertexIndex);
            }
        }
    }
}
