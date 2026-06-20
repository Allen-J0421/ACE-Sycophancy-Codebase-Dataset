import java.util.Collections;
import java.util.List;

public final class DirectedGraph {
    private final List<List<Integer>> adjacencyList;

    DirectedGraph(List<List<Integer>> adjacencyList) {
        this.adjacencyList = freezeAdjacencyList(adjacencyList);
    }

    public int vertexCount() {
        return adjacencyList.size();
    }

    public List<Integer> neighborsOf(int vertex) {
        validateVertex(vertex);
        return adjacencyList.get(vertex);
    }

    private void validateVertex(int vertex) {
        if (vertex < 0 || vertex >= vertexCount()) {
            throw new IndexOutOfBoundsException("vertex out of range: " + vertex);
        }
    }

    private static List<List<Integer>> freezeAdjacencyList(List<List<Integer>> adjacencyList) {
        List<List<Integer>> frozenAdjacencyList = new java.util.ArrayList<>(adjacencyList.size());
        for (List<Integer> neighbors : adjacencyList) {
            frozenAdjacencyList.add(Collections.unmodifiableList(new java.util.ArrayList<>(neighbors)));
        }
        return Collections.unmodifiableList(frozenAdjacencyList);
    }
}
