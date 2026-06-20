import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class DirectedGraph implements Graph {
    private final List<List<Integer>> adjacency;

    private DirectedGraph(List<List<Integer>> adjacency) {
        this.adjacency = adjacency;
    }

    public static DirectedGraph fromEdges(int vertexCount, Edge... edges) {
        return fromEdges(vertexCount, List.of(edges));
    }

    public static DirectedGraph fromEdges(int vertexCount, List<Edge> edges) {
        if (edges == null) {
            throw new IllegalArgumentException("Edges must not be null.");
        }

        List<List<Integer>> adjacency = createEmptyAdjacency(vertexCount);

        for (Edge edge : edges) {
            validateEdge(edge);

            int from = edge.from();
            int to = edge.to();
            validateVertex(from, vertexCount);
            validateVertex(to, vertexCount);
            adjacency.get(from).add(to);
        }

        return new DirectedGraph(freeze(adjacency));
    }

    @Override
    public int vertexCount() {
        return adjacency.size();
    }

    @Override
    public List<Integer> neighborsOf(int vertex) {
        validateVertex(vertex, vertexCount());
        return adjacency.get(vertex);
    }

    @Override
    public DirectedGraph reverse() {
        List<List<Integer>> reversedAdjacency = createEmptyAdjacency(vertexCount());

        for (int vertex = 0; vertex < vertexCount(); vertex++) {
            for (int neighbor : neighborsOf(vertex)) {
                reversedAdjacency.get(neighbor).add(vertex);
            }
        }

        return new DirectedGraph(freeze(reversedAdjacency));
    }

    private static List<List<Integer>> createEmptyAdjacency(int vertexCount) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("Vertex count must be non-negative.");
        }

        List<List<Integer>> adjacency = new ArrayList<>(vertexCount);
        for (int vertex = 0; vertex < vertexCount; vertex++) {
            adjacency.add(new ArrayList<>());
        }
        return adjacency;
    }

    private static List<List<Integer>> freeze(List<List<Integer>> adjacency) {
        List<List<Integer>> immutableAdjacency = new ArrayList<>(adjacency.size());

        for (List<Integer> neighbors : adjacency) {
            immutableAdjacency.add(List.copyOf(neighbors));
        }

        return Collections.unmodifiableList(immutableAdjacency);
    }

    private static void validateEdge(Edge edge) {
        if (edge == null) {
            throw new IllegalArgumentException("Edges must not contain null values.");
        }
    }

    private static void validateVertex(int vertex, int vertexCount) {
        if (vertex < 0 || vertex >= vertexCount) {
            throw new IllegalArgumentException(
                "Vertex " + vertex + " is out of bounds for graph size " + vertexCount + '.'
            );
        }
    }
}
