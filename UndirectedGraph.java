import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

final class UndirectedGraph {
    private final int vertexCount;
    private final List<List<Integer>> adjacencyList;

    private UndirectedGraph(int vertexCount, List<List<Integer>> adjacencyList) {
        this.vertexCount = vertexCount;
        this.adjacencyList = adjacencyList;
    }

    static UndirectedGraph fromEdges(int vertexCount, List<Edge> edges) {
        validateVertexCount(vertexCount);
        Objects.requireNonNull(edges, "edges must not be null");

        List<List<Integer>> adjacencyList = createEmptyAdjacencyList(vertexCount);
        for (Edge edge : edges) {
            Edge nonNullEdge = Objects.requireNonNull(edge, "edge must not be null");
            addUndirectedEdge(adjacencyList, vertexCount, nonNullEdge);
        }

        return new UndirectedGraph(vertexCount, immutableCopyOf(adjacencyList));
    }

    int vertexCount() {
        return vertexCount;
    }

    List<Integer> neighborsOf(int vertex) {
        validateVertex(vertex);
        return adjacencyList.get(vertex);
    }

    private static List<List<Integer>> createEmptyAdjacencyList(int vertexCount) {
        List<List<Integer>> adjacencyList = new ArrayList<>(vertexCount);
        for (int i = 0; i < vertexCount; i++) {
            adjacencyList.add(new ArrayList<>());
        }
        return adjacencyList;
    }

    private static void addUndirectedEdge(List<List<Integer>> adjacencyList, int vertexCount, Edge edge) {
        validateVertex(edge.source(), vertexCount);
        validateVertex(edge.destination(), vertexCount);

        adjacencyList.get(edge.source()).add(edge.destination());
        adjacencyList.get(edge.destination()).add(edge.source());
    }

    private static List<List<Integer>> immutableCopyOf(List<List<Integer>> adjacencyList) {
        List<List<Integer>> immutableAdjacencyList = new ArrayList<>(adjacencyList.size());
        for (List<Integer> neighbors : adjacencyList) {
            immutableAdjacencyList.add(Collections.unmodifiableList(new ArrayList<>(neighbors)));
        }
        return Collections.unmodifiableList(immutableAdjacencyList);
    }

    private void validateVertex(int vertex) {
        validateVertex(vertex, vertexCount);
    }

    private static void validateVertexCount(int vertexCount) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("vertexCount must not be negative");
        }
    }

    private static void validateVertex(int vertex, int vertexCount) {
        if (vertex < 0 || vertex >= vertexCount) {
            throw new IllegalArgumentException("vertex out of range: " + vertex);
        }
    }
}
