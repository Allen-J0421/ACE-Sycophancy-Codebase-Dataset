import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class GraphSupport {
    private GraphSupport() {
    }

    static void validateVertexCount(int vertexCount) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("vertexCount must be non-negative");
        }
    }

    static void validateVertexIndex(int vertex, int vertexCount) {
        if (vertex < 0 || vertex >= vertexCount) {
            throw new IndexOutOfBoundsException("vertex out of range: " + vertex);
        }
    }

    static List<List<Integer>> freezeAdjacencyList(List<List<Integer>> adjacencyList) {
        List<List<Integer>> frozenAdjacencyList = new ArrayList<>(adjacencyList.size());
        for (List<Integer> neighbors : adjacencyList) {
            frozenAdjacencyList.add(Collections.unmodifiableList(new ArrayList<>(neighbors)));
        }
        return Collections.unmodifiableList(frozenAdjacencyList);
    }
}
