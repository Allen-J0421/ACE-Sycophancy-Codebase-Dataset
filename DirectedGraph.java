import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class DirectedGraph {
    private final List<List<Integer>> adjacency;

    private DirectedGraph(List<List<Integer>> adjacency) {
        this.adjacency = adjacency;
    }

    public static DirectedGraph fromEdges(int vertexCount, int[][] edges) {
        if (edges == null) {
            throw new IllegalArgumentException("Edges must not be null.");
        }

        List<List<Integer>> adjacency = createEmptyAdjacency(vertexCount);

        for (int[] edge : edges) {
            validateEdge(edge);

            int from = edge[0];
            int to = edge[1];
            validateVertex(from, vertexCount);
            validateVertex(to, vertexCount);
            adjacency.get(from).add(to);
        }

        return new DirectedGraph(freeze(adjacency));
    }

    public int vertexCount() {
        return adjacency.size();
    }

    public List<Integer> neighborsOf(int vertex) {
        validateVertex(vertex, vertexCount());
        return adjacency.get(vertex);
    }

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

    private static void validateEdge(int[] edge) {
        if (edge == null || edge.length != 2) {
            throw new IllegalArgumentException("Each edge must contain exactly two vertices.");
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
