import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class UndirectedGraph {

    private final List<List<Integer>> adjacencyList;

    UndirectedGraph(int vertexCount) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("Vertex count must be non-negative.");
        }

        adjacencyList = new ArrayList<>(vertexCount);
        for (int vertex = 0; vertex < vertexCount; vertex++) {
            adjacencyList.add(new ArrayList<>());
        }
    }

    int vertexCount() {
        return adjacencyList.size();
    }

    void addEdge(int fromVertex, int toVertex) {
        validateVertex(fromVertex);
        validateVertex(toVertex);

        adjacencyList.get(fromVertex).add(toVertex);
        adjacencyList.get(toVertex).add(fromVertex);
    }

    List<Integer> neighborsOf(int vertex) {
        validateVertex(vertex);
        return Collections.unmodifiableList(adjacencyList.get(vertex));
    }

    private void validateVertex(int vertex) {
        if (vertex < 0 || vertex >= adjacencyList.size()) {
            throw new IllegalArgumentException("Vertex index out of bounds: " + vertex);
        }
    }
}
