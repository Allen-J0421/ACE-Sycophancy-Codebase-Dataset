import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class DirectedGraph {
    private final List<List<Integer>> adjacencyList;

    private DirectedGraph(List<List<Integer>> adjacencyList) {
        this.adjacencyList = adjacencyList;
    }

    public static DirectedGraph withVertexCount(int vertexCount) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("Vertex count must be non-negative.");
        }

        List<List<Integer>> adjacencyList = new ArrayList<>();
        for (int vertex = 0; vertex < vertexCount; vertex++) {
            adjacencyList.add(new ArrayList<>());
        }
        return new DirectedGraph(adjacencyList);
    }

    public static DirectedGraph fromEdges(int vertexCount, Iterable<DirectedEdge> edges) {
        DirectedGraph graph = withVertexCount(vertexCount);
        for (DirectedEdge edge : edges) {
            validateEdge(edge);
            graph.addEdge(edge.source(), edge.destination());
        }
        return graph;
    }

    public int vertexCount() {
        return adjacencyList.size();
    }

    public List<Integer> neighborsOf(int vertex) {
        validateVertex(vertex);
        return Collections.unmodifiableList(adjacencyList.get(vertex));
    }

    public void addEdge(int source, int destination) {
        validateVertex(source);
        validateVertex(destination);
        adjacencyList.get(source).add(destination);
    }

    private static void validateEdge(DirectedEdge edge) {
        if (edge == null) {
            throw new IllegalArgumentException("Edge cannot be null.");
        }
    }

    private void validateVertex(int vertex) {
        if (vertex < 0 || vertex >= adjacencyList.size()) {
            throw new IllegalArgumentException("Vertex out of bounds: " + vertex);
        }
    }
}
