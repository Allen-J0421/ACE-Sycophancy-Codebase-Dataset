import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class DirectedGraph {
    private final List<List<Integer>> adjacencyList;

    DirectedGraph(List<List<Integer>> adjacencyList) {
        this.adjacencyList = adjacencyList;
    }

    public static DirectedGraphBuilder builder(int vertexCount) {
        return new DirectedGraphBuilder(vertexCount);
    }

    public static DirectedGraph withVertexCount(int vertexCount) {
        return builder(vertexCount).build();
    }

    public static DirectedGraph fromEdges(int vertexCount, Iterable<DirectedEdge> edges) {
        DirectedGraphBuilder builder = builder(vertexCount);
        for (DirectedEdge edge : edges) {
            validateEdge(edge);
            builder.addEdge(edge);
        }
        return builder.build();
    }

    public int vertexCount() {
        return adjacencyList.size();
    }

    public List<Integer> neighborsOf(int vertex) {
        validateVertex(vertex);
        return adjacencyList.get(vertex);
    }

    private static void validateEdge(DirectedEdge edge) {
        if (edge == null) {
            throw new IllegalArgumentException("Edge cannot be null.");
        }
    }

    static List<List<Integer>> createEmptyAdjacencyList(int vertexCount) {
        validateVertexCount(vertexCount);

        List<List<Integer>> adjacencyList = new ArrayList<>();
        for (int vertex = 0; vertex < vertexCount; vertex++) {
            adjacencyList.add(new ArrayList<>());
        }
        return adjacencyList;
    }

    static List<List<Integer>> freezeAdjacencyList(List<List<Integer>> adjacencyList) {
        List<List<Integer>> frozenAdjacencyList = new ArrayList<>(adjacencyList.size());
        for (List<Integer> neighbors : adjacencyList) {
            frozenAdjacencyList.add(Collections.unmodifiableList(new ArrayList<>(neighbors)));
        }
        return Collections.unmodifiableList(frozenAdjacencyList);
    }

    static void validateVertexCount(int vertexCount) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("Vertex count must be non-negative.");
        }
    }

    private void validateVertex(int vertex) {
        if (vertex < 0 || vertex >= adjacencyList.size()) {
            throw new IllegalArgumentException("Vertex out of bounds: " + vertex);
        }
    }
}
