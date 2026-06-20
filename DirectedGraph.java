import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class DirectedGraph {
    private final List<List<Integer>> adjacencyList;

    private DirectedGraph(int vertexCount) {
        adjacencyList = new ArrayList<>(vertexCount);
        for (int vertex = 0; vertex < vertexCount; vertex++) {
            adjacencyList.add(new ArrayList<>());
        }
    }

    public static DirectedGraph withVertexCount(int vertexCount) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("vertexCount must be non-negative");
        }
        return new DirectedGraph(vertexCount);
    }

    public int vertexCount() {
        return adjacencyList.size();
    }

    public void addEdge(int from, int to) {
        validateVertex(from);
        validateVertex(to);
        adjacencyList.get(from).add(to);
    }

    public List<Integer> neighborsOf(int vertex) {
        validateVertex(vertex);
        return Collections.unmodifiableList(adjacencyList.get(vertex));
    }

    private void validateVertex(int vertex) {
        if (vertex < 0 || vertex >= vertexCount()) {
            throw new IndexOutOfBoundsException("vertex out of range: " + vertex);
        }
    }
}
