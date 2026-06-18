import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class AdjacencyListGraph implements Graph {
    private final List<List<Integer>> adjacencyList;

    AdjacencyListGraph(int vertexCount) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("Vertex count cannot be negative.");
        }

        adjacencyList = new ArrayList<>(vertexCount);
        for (int vertex = 0; vertex < vertexCount; vertex++) {
            adjacencyList.add(new ArrayList<>());
        }
    }

    @Override
    public int vertexCount() {
        return adjacencyList.size();
    }

    void addUndirectedEdge(int from, int to) {
        validateVertex(from);
        validateVertex(to);

        adjacencyList.get(from).add(to);
        adjacencyList.get(to).add(from);
    }

    @Override
    public List<Integer> neighborsOf(int vertex) {
        validateVertex(vertex);
        return Collections.unmodifiableList(adjacencyList.get(vertex));
    }

    private void validateVertex(int vertex) {
        if (vertex < 0 || vertex >= adjacencyList.size()) {
            throw new IllegalArgumentException("Vertex out of range: " + vertex);
        }
    }
}
