import java.util.ArrayList;
import java.util.List;

final class UndirectedGraph implements Graph {

    private final List<List<Integer>> adjacencyList;

    private UndirectedGraph(List<List<Integer>> adjacencyList) {
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
    public List<Integer> neighborsOf(int vertex) {
        validateVertex(vertex);
        return adjacencyList.get(vertex);
    }

    private void validateVertex(int vertex) {
        if (vertex < 0 || vertex >= adjacencyList.size()) {
            throw new IllegalArgumentException("Vertex index out of bounds: " + vertex);
        }
    }

    static final class Builder {

        private final List<List<Integer>> adjacencyList;

        Builder(int vertexCount) {
            if (vertexCount < 0) {
                throw new IllegalArgumentException("Vertex count must be non-negative.");
            }

            adjacencyList = new ArrayList<>(vertexCount);
            for (int vertex = 0; vertex < vertexCount; vertex++) {
                adjacencyList.add(new ArrayList<>());
            }
        }

        Builder addEdge(int fromVertex, int toVertex) {
            validateVertex(fromVertex);
            validateVertex(toVertex);

            adjacencyList.get(fromVertex).add(toVertex);
            adjacencyList.get(toVertex).add(fromVertex);
            return this;
        }

        UndirectedGraph build() {
            List<List<Integer>> snapshot = new ArrayList<>(adjacencyList.size());
            for (List<Integer> neighbors : adjacencyList) {
                snapshot.add(List.copyOf(neighbors));
            }

            return new UndirectedGraph(List.copyOf(snapshot));
        }

        private void validateVertex(int vertex) {
            if (vertex < 0 || vertex >= adjacencyList.size()) {
                throw new IllegalArgumentException("Vertex index out of bounds: " + vertex);
            }
        }
    }
}
